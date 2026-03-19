# 🔐 Secure Chat System (Built From Scratch)

A hands-on project to understand how real-world secure communication systems (like WhatsApp, Signal, TLS) actually work — by building one step-by-step from the ground up.

This project starts with a basic chat system and evolves towards a **cryptographically secure messaging system**.

---

## 🚀 Phase 1 — Multi-Client Chat System

This phase implements a real-time chat application using **Java sockets and multithreading**.

### ✨ Features

* 👥 Multiple clients can connect simultaneously
* 💬 Real-time message exchange
* 📡 Server-based message broadcasting
* 🏷️ Username-based identification
* ⚡ Full-duplex communication (send & receive at the same time)

---

## 🧠 Architecture

* The **server acts as a relay** (it only forwards messages)
* Each client runs on a **separate thread**
* Communication is **bi-directional and continuous**

*(Add your architecture images below if needed)*

---

## ⚙️ Tech Stack

* Java (Core)
* Socket Programming (`Socket`, `ServerSocket`)
* Multithreading
* Buffered I/O Streams

---

## 🛠️ How to Run

### 📌 Step 1 — Compile and Start the Server (Terminal 1)

```bash
javac server.java & java server
```

Output:

```
Server started on 1234
```

---

### 📌 Step 3 — Start Clients (Terminal 2 & 3)

👉 Open **at least 2–3 separate terminals** for clients

In each terminal, run:

```bash
javac client.java & java client
```

---

### 📌 Step 4 — Enter Username

Each client will be prompted:

```
Enter the username:
```

Example:

```
palak
```

---

### 📌 Step 5 — Start Chatting 🎉

* Messages are instantly broadcast to all connected clients
* Each message is tagged with the sender’s username

---

## 💬 Sample Flow

<img width="1920" height="1200" alt="Screenshot 2026-03-19 110622" src="https://github.com/user-attachments/assets/a9eeb626-3e89-4b29-8562-9384566a733f" />


---

## ⚠️ Limitations (Intentional for Learning)

This phase is intentionally **insecure** to build understanding step-by-step:

* ❌ Messages are in plain text
* ❌ Server can read all messages
* ❌ No authentication system
* ❌ No encryption

---

## 🔐 Project Roadmap

This project is being built in multiple phases:

### ✅ Phase 1 — Basic Chat System *(Completed)*

* Multi-client communication
* Message broadcasting

### 🔄 Phase 2 — Diffie-Hellman Key Exchange *(In Progress)*

* Public key exchange
* Shared secret generation

### ⏳ Phase 3 — AES Encryption

* Encrypt messages before sending
* Decrypt on receiver side

### ⏳ Phase 4 — MITM Attack Simulation

* Understand real-world vulnerabilities

### ⏳ Phase 5 — Authentication

* Digital signatures
* Identity verification

---

## 🎯 Goal of This Project

This is not just a chat app — it’s a **learning system** to understand:

* How secure communication actually works
* Why encryption alone is not enough
* How key exchange protocols behave in real systems
* How vulnerabilities (like MITM attacks) occur
* How to design secure architectures

---

## 🧠 Key Learnings So Far

* Implemented full-duplex communication using threads
* Understood how servers handle multiple clients concurrently
* Built a scalable communication pipeline
* Prepared the system for cryptographic integration

---

## 👨‍💻 Author

Built as a deep learning project to explore **cryptography, networking, and secure system design from scratch**.

---

