const express = require('express');
const path = require('path');
const http = require('http');
const socketIo = require('socket.io');
const amqp = require('amqplib');
const app = express();
const port = 3000;
app.set('view engine', 'ejs');
app.use(express.static(path.join(__dirname, '/assets')));
app.set('views', path.join(__dirname, '/'));

let temp = 0;
let bpm = 0;
let spo2 = 0;
let vitesse = 0;
let alert = 0;


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

    channel.bindQueue(queue, EXCHANGE_NAME, '#');

    console.log(`[*] Waiting for messages. To exit, press CTRL+C`);

    channel.consume(queue, (msg) => {
      alert = 0;
      const message = msg.content.toString('utf-8');
      console.log(`[x] Received '${msg.fields.routingKey}':'${message}'`);
    
      // Verify the type of the object (temp/spo2/bpm/vitesse) and modify the associated variable
      const parsedMessage = JSON.parse(message);
      if (parsedMessage.type === 'VITESSE') {
        vitesse = parsedMessage.data;
      } else if (parsedMessage.type === 'SPO2') {
        spo2 = parsedMessage.data;
        if (spo2 < 90) {
          alert = 1 ;
        }
      } else if (parsedMessage.type === 'VFC') {
        bpm = parsedMessage.data;
        if (bpm < 30) {
          alert = 1;
        }
      } else if (parsedMessage.type === 'TEMP') {
        temp = parsedMessage.data;
      }
      else if (parsedMessage.type === 'VITESSE') {
        vitesse = parsedMessage.data;

      }
    
    
      io.emit('newMessage', parsedMessage);
    }, { noAck: true });
  } catch (error) {
    console.error('Error:', error.message);
  }
}
consumeMessages();

app.get('/', (req, res) => {
  vitesse = parseFloat(vitesse.toFixed(2));
  res.render('index', { VITESSE: vitesse, SPO2: spo2, BPM: bpm, TEMP: temp, alert: alert});
});


server.listen(port, () => {
  console.log(`Server is running on http://localhost:${port}`);
});