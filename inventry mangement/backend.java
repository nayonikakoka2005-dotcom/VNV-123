// server.js
const express = require('express');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const cors = require('cors');

const app = express();

// Middlewares
app.use(cors());
app.use(bodyParser.json());

//  Replace with your MongoDB connection string
mongoose.connect('mongodb://127.0.0.1:27017/inventoryDB', {
  useNewUrlParser: true,
  useUnifiedTopology: true
}).then(() => console.log(' MongoDB Connected'))
  .catch(err => console.error(' MongoDB connection error:', err));

// Schema & Model
const itemSchema = new mongoose.Schema({
  name: { type: String, required: true },
  quantity: { type: Number, required: true },
  price: { type: Number, required: true }
});

const Item = mongoose.model('Item', itemSchema);

// Routes

//  Add a new item
app.post('/api/items', async (req, res) => {
  try {
    const { name, quantity, price } = req.body;
    const newItem = new Item({ name, quantity, price });
    await newItem.save();
    res.status(201).json(newItem);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
});

//  Get all items
app.get('/api/items', async (req, res) => {
  const items = await Item.find();
  res.json(items);
});

//  Update an item
app.put('/api/items/:id', async (req, res) => {
  try {
    const updatedItem = await Item.findByIdAndUpdate(req.params.id, req.body, { new: true });
    res.json(updatedItem);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
});

//  Delete an item
app.delete('/api/items/:id', async (req, res) => {
  try {
    await Item.findByIdAndDelete(req.params.id);
    res.json({ message: 'Item deleted successfully' });
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
});

//  Get total inventory value
app.get('/api/total', async (req, res) => {
  const items = await Item.find();
  const totalValue = items.reduce((sum, i) => sum + i.quantity * i.price, 0);
  res.json({ totalValue });
});

// Start server
const PORT = 5000;
app.listen(PORT, () => console.log(`🚀 Server running on http://localhost:${PORT}`));

