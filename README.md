

```markdown
# 🔐 Secure Chat System (From Scratch)

A step-by-step implementation of a secure messaging system, starting from basic socket communication and evolving towards cryptographically secure communication.

This project is built to deeply understand how real-world secure systems (like WhatsApp, Signal, TLS) work internally.

---

## 🚀 Phase 1: Basic Chat System

This phase implements a real-time multi-client chat system using Java sockets.

### ✨ Features
- Multi-client support
- Real-time message exchange
- Server-based message broadcasting
- Username-based message identification
- Multi-threaded client handling

---

## 🧠 Architecture

```

![alt text](image.png)
![alt text](image-1.png)

````

- Server acts as a message relay (no processing)
- Each client runs on a separate thread
- Full-duplex communication (send & receive simultaneously)

---

## ⚙️ Tech Stack

- Java (Core)
- Sockets (`Socket`, `ServerSocket`)
- Multithreading
- Buffered I/O

---

## 🛠️ How to Run

### 1. Compile

```bash
javac src/Server.java
javac src/Client.java
````

---

### 2. Start Server

```bash
java src.Server
```

Output:

```
Server started on port 1234
```

---

### 3. Start Clients (Open 2 Terminals)

```bash
java src.Client
```

---

### 4. Enter Username

```
Enter your username:
Alice
```

---

### 💬 Sample Chat

c:\Users\palak\OneDrive\Pictures\Screenshots 1\Screenshot 2026-03-19 110622.png

---

## ⚠️ Limitations (Intentional for Learning)

* No encryption (messages are plain text)
* Server can read all messages
* No authentication (anyone can impersonate)

---

## 🔐 Upcoming Phases

* Phase 2: Diffie-Hellman Key Exchange
* Phase 3: AES Encryption for Messages
* Phase 4: Man-in-the-Middle (MITM) Attack Simulation
* Phase 5: Authentication using Digital Signatures

---

## 🎯 Goal of This Project

To build and break a secure communication system from scratch and understand:

* Why encryption alone is not enough
* How key exchange works
* How real-world attacks happen
* How to design secure systems

---

## 👨‍💻 Author

Built as a hands-on learning project to explore cryptography and secure system design.

```

---

### 🔍 Key Learning

```markdown
- Understood full-duplex communication using threads
- Learned how servers handle multiple clients concurrently
- Designed a basic communication pipeline for future encryption layers
```

---
