# 🔐 Secure Chat Application — Phase 2 (Diffie-Hellman Integration)

## 📌 Overview

This phase represents the integration of **Diffie-Hellman (DH) Key Exchange** into a multi-client chat system. The goal was to move from a simple messaging system to a **cryptography-aware communication system**.

The implementation focuses on:

* Public key exchange between clients
* Shared secret generation using DH
* Handling real-world issues in distributed systems
* Understanding how secure communication actually works

---

## 🚀 What This Version Supports

* Multi-client chat via server
* Dynamic client connections
* Public key exchange using Diffie-Hellman
* Shared key generation on each client
* Real-time message handling using threads

---

## 🚨 Problems Faced

### ❌ 1. Shared key not establishing on all clients

**Symptoms:**

* Only one side generated the shared key
* Other clients did not show “Shared key established”
* Confusion about whether DH was working correctly

---

### ❌ 2. Large “weird” KEY messages in console

Example:

```
KEY:MIIC...
```

**Symptoms:**

* Flooding of console
* Looked like abnormal behavior

---

### ❌ 3. Misunderstanding of shared key behavior

Assumption:

```
All clients should have SAME shared key
```

---

## 🧠 Root Cause Analysis

### 🔍 1. One-way key processing

* Clients were receiving public keys
* But not properly processing them in all cases

---

### 🔍 2. Message handling not clearly separated

Initially:

* All messages treated the same
* No clear distinction between:

  * Chat messages
  * Key exchange messages

---

### 🔍 3. Incorrect expectation from Diffie-Hellman

Reality:

| Pair  | Shared Key |
| ----- | ---------- |
| A ↔ B | Same       |
| A ↔ C | Different  |
| B ↔ C | Different  |

👉 DH creates **pairwise shared secrets**, not a global key

---

## ✅ Solutions Implemented
<img width="960" height="600" alt="image" src="https://github.com/user-attachments/assets/7e427543-03c6-461c-a076-77fac2f6b56b" />


---

### 🔧 1. Message Type Handling

Client now checks:

```java
if(message.startsWith("KEY:"))
```

👉 This ensures:

* Key messages are processed separately
* Normal chat messages are printed

---

### 🔧 2. Public Key Extraction & Validation

```java
String keyBase64 = message.substring(4).trim();
```

Added checks:

```java
if(keyBase64.isEmpty()) return;
if(keyBase64.length() < 50) return;
```

👉 Prevents:

* Invalid or corrupted keys
* Crashes due to bad input

---

### 🔧 3. Public Key Reconstruction

```java
byte[] keyBytes = Base64.getDecoder().decode(keyBase64);

KeyFactory keyFactory = KeyFactory.getInstance("DH");
X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
PublicKey otherPublicKey = keyFactory.generatePublic(keySpec);
```

👉 Converts received key into usable object

---

### 🔧 4. Shared Secret Generation

```java
finalKeyAgree.doPhase(otherPublicKey, true);
byte[] sharedSecret = finalKeyAgree.generateSecret();
```

👉 Core Diffie-Hellman logic

---

### 🔧 5. Multi-threaded Client Handling

#### Thread 1 → Receive messages

* Listens to server
* Handles KEY messages
* Prints chat messages

#### Thread 2 → Send messages

* Takes user input
* Sends to server

👉 Enables real-time communication

---

## 🔐 Key Learning

### ✔ Diffie-Hellman Behavior

```text
Same shared key → only between 2 clients
Different pairs → different keys
```

---

### ✔ Why “weird text” appeared

```
KEY:MIIC...
```

👉 This is:

* Base64 encoded public key
* Completely normal
* Required for key exchange

---

### ✔ Why keys looked different

* Each client has different private key
* Shared secret depends on both participants

👉 Hence:

```
Different clients → different shared keys
```

---

## ⚠️ Current Limitations

### ❌ 1. Shared keys are not stored properly

```java
Map<String, SecretKey> sharedKeys = new HashMap<>();
```

👉 Declared but not used fully

---

### ❌ 2. Shared key printed incorrectly

```java
System.out.println("Shared key is "+sharedSecret);
```

👉 This prints memory reference, not actual key

---

### ❌ 3. No encryption yet

* Messages are still plain text
* Shared key is not used for AES encryption

---

## 🧪 Validation

✔ Clients successfully:

* Send public keys
* Receive public keys
* Generate shared secret

✔ No crashes during key exchange
✔ Stable communication achieved

---

## 🚀 What This Phase Achieved

* Integrated real cryptographic protocol
* Debugged distributed key exchange issues
* Understood practical behavior of DH
* Built foundation for secure messaging

---

## 🔮 Next Phase

* Store keys per client properly
* Convert shared secret → AES key
* Encrypt messages before sending
* Decrypt messages on receiving side

---

## 💡 Final Insight

```
The issue was not in cryptography,
but in how messages and data were handled in the system
```

This phase marks the shift from:

```
Basic programming → System-level thinking
```

---
