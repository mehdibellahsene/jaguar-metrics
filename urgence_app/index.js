const express = require('express');
const path = require('path');
const http = require('http');
const socketIo = require('socket.io');
const amqp = require('amqplib');
const app = express();

app.set('view engine', 'ejs');
app.use(express.static(path.join(__dirname, '/assets')));
app.set('views', path.join(__dirname, '/'));

let bpm = 15;
let spo2 = 0;

const server = http.createServer(app);
const io = socketIo(server);

const EXCHANGE_NAME = 'logs';
const BROKER_HOST = 'localhost:5672';

async function consumeMessages() {
  try {
    const connection = await amqp.connect(`amqp://localhost:5672`);
    const channel = await connection.createChannel();

    await channel.assertExchange(EXCHANGE_NAME, 'topic', { durable: false });
    const { queue } = await channel.assertQueue('');

    channel.bindQueue(queue, EXCHANGE_NAME, 'logs.vital.*');

    console.log(`[*] Waiting for messages. To exit, press CTRL+C`);

    channel.consume(queue, (msg) => {
      const message = msg.content.toString('utf-8');
      console.log(`[x] Received '${msg.fields.routingKey}':'${message}'`);

      const parsedMessage = JSON.parse(message);
      if (parsedMessage.type === 'VITESSE') {
        vitesse = parsedMessage.data;
      } else if (parsedMessage.type === 'SPO2') {
        spo2 = parsedMessage.data;
      } else if (parsedMessage.type === 'VFC') {
        bpm = parsedMessage.data;
        if (bpm <30 || spo2 <87) {
          sendAlert();
        }
      }
      io.emit('newMessage', parsedMessage);
    }, { noAck: true });
  } catch (error) {
    console.error('Error:', error.message);
  }
}

function sendAlert() {
console.log('Sending email alert');
}

consumeMessages();
