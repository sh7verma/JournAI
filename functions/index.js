const { onRequest } = require("firebase-functions/v2/https");
const admin = require("firebase-admin");
const OpenAI = require("openai");
const express = require("express");
const cors = require("cors");

admin.initializeApp();

const app = express();

app.use(cors());
app.use(express.json());


// ---------- AUTH MIDDLEWARE ----------
app.use(async (req, res, next) => {

  const token =
    req.headers.authorization?.split("Bearer ")[1];

  if (!token) {
    return res.status(401).send("No token");
  }

  try {
    await admin.auth().verifyIdToken(token);
    next();
  } catch {
    return res.status(401).send("Invalid token");
  }
});


// ---------- OPENAI FACTORY ----------
function createOpenAI() {
  return new OpenAI({
    apiKey: process.env.OPENAI_KEY,
  });
}


// ---------- CHAT (SSE) ----------
app.post("/chat", async (req, res) => {

  const openai = createOpenAI();

  res.setHeader("Content-Type", "text/event-stream");
  res.setHeader("Cache-Control", "no-cache");
  res.setHeader("Connection", "keep-alive");

  const { prompt } = req.body;

  if (!prompt) return res.end();


  const stream =
    await openai.chat.completions.create({
      model: "gpt-4o-mini",
      stream: true,
      messages: [
        { role: "user", content: prompt }
      ],
    });


  for await (const chunk of stream) {

    const text =
      chunk.choices[0]?.delta?.content;

    if (text) {
      res.write(`data:${text}\n\n`);
    }
  }

  res.end();
});


// ---------- /prompt ----------
app.get("/prompt", async (req, res) => {

  const openai = createOpenAI();

  try {
    const completion =
      await openai.chat.completions.create({
        model: "gpt-4o-mini",
        messages: [
          {
            role: "system",
            content:
              "You are a journaling assistant. Generate one thoughtful writing prompt."
          }
        ],
      });

    const prompt =
      completion.choices[0].message.content;

    res.json({ prompt });

  } catch (e) {
    console.error(e);
    res.status(500).send("AI Error");
  }
});


// ---------- /grammar ----------
app.post("/grammar", async (req, res) => {

  const { text } = req.body;

  if (!text) return res.status(400).send();

  const openai = createOpenAI();

  try {

    const completion =
      await openai.chat.completions.create({
        model: "gpt-4o-mini",
        messages: [
          {
            role: "system",
            content:
              "Correct grammar and rewrite clearly."
          },
          {
            role: "user",
            content: text
          }
        ],
      });

    const corrected =
      completion.choices[0].message.content;

    res.json({ corrected });

  } catch (e) {
    console.error(e);
    res.status(500).send("AI Error");
  }
});


// ---------- /tips ----------
app.post("/tips", async (req, res) => {

  const { text } = req.body;

  if (!text) return res.status(400).send();

  const openai = createOpenAI();

  try {

    const completion =
      await openai.chat.completions.create({
        model: "gpt-4o-mini",
        messages: [
          {
            role: "system",
            content:
              "Give 3 short writing improvement tips."
          },
          {
            role: "user",
            content: text
          }
        ],
      });

    const tipsText =
      completion.choices[0].message.content;

    const tips =
      tipsText
        .split("\n")
        .filter(it => it.trim().length > 0);

    res.json({ tips });

  } catch (e) {
    console.error(e);
    res.status(500).send("AI Error");
  }
});


// ---------- /sentiment ----------
app.post("/sentiment", async (req, res) => {

  const { text } = req.body;

  if (!text) return res.status(400).send();

  const openai = createOpenAI();

  try {

    const completion =
      await openai.chat.completions.create({
        model: "gpt-4o-mini",
        messages: [
          {
            role: "system",
            content:
              "Analyze sentiment. Return mood and explanation."
          },
          {
            role: "user",
            content: text
          }
        ],
      });

    const analysis =
      completion.choices[0].message.content;

    const mood =
      analysis.toLowerCase().includes("positive")
        ? "Positive"
        : analysis.toLowerCase().includes("negative")
          ? "Negative"
          : "Neutral";

    res.json({
      mood,
      analysis
    });

  } catch (e) {
    console.error(e);
    res.status(500).send("AI Error");
  }
});


// ---------- EXPORT ----------
exports.ai = onRequest(
  {
    secrets: ["OPENAI_KEY"],
  },
  app
);
