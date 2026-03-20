# 🔁 PHASE 3: Multi-Client Handling

This section explains how the system behaves as multiple clients join the chat and how key exchange is handled.

---

## 🧩 Core Idea

* Each client runs on a separate thread.
* The server maintains a shared list of all active clients.
* Messages (including public keys) are broadcast to all other clients.
* Diffie-Hellman key exchange happens **between every pair of clients**.

---

## 👥 Case 1: Two Clients (A, B)

### Flow:

1. A joins → sends `KEY_A`
2. B joins → sends `KEY_B`
3. Server relays:

   * `KEY_B` → A
   * `KEY_A` → B

### Result:

* A computes `key_AB`
* B computes `key_AB`

✅ Both share the same secret key
![alt text](image.png)

---

## 👥 Case 2: Three Clients (A, B, C)

### Flow:

1. A ↔ B already connected (key_AB exists)
2. C joins → sends `KEY_C`
3. Server relays:

   * `KEY_C` → A, B
   * `KEY_A`, `KEY_B` → C

### Result:

* A computes `key_AC`
* B computes `key_BC`
* C computes:

  * `key_CA`
  * `key_CB`

⚠️ Each pair has a **different shared key**
![alt text](image-1.png)

---

## 👥 Case 3: Four Clients (A, B, C, D)

### Flow:

1. A, B, C already connected
2. D joins → sends `KEY_D`
3. Server relays:

   * `KEY_D` → A, B, C
   * `KEY_A`, `KEY_B`, `KEY_C` → D

### Result:

* New keys formed:

  * A ↔ D → `key_AD`
  * B ↔ D → `key_BD`
  * C ↔ D → `key_CD`

  ![alt text](image-2.png)

---

## ⚠️ Current Limitation

At present, each client stores only **one shared key**:

```java
byte[][] sharedSecret = new byte[1][];
```

### Impact:

* When a new client joins, the previous key gets overwritten
* Only the **latest connection's key is retained**

---

## 🧠 Key Insight

* Diffie-Hellman works **per connection (pair-wise)**
* In multi-client systems:

  * Total keys grow as connections increase
  * Proper handling requires storing keys per client

---

## 🚀 Next Improvement

To fully support multi-client encryption:

```java
Map<String, SecretKey> sharedKeys;
```

This allows:

* One key per client pair
* Correct encryption/decryption across all users

---
