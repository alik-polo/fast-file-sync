# Fast File Sync

High-performance file synchronization engine written in Java 17.

The goal of this project is to build a memory-efficient and scalable file sync tool
capable of handling large directory trees (hundreds of thousands to millions of files)
with predictable memory usage and minimal GC pressure.

---

## 🎯 Vision

Most file synchronization tools prioritize convenience over resource efficiency.

This project focuses on:

- Low memory footprint
- Streaming diff algorithms
- Minimal heap allocations
- Predictable O(n) behavior
- Clean and testable architecture

---

## 🏗 Planned Architecture

The core flow will follow:

Scan → Snapshot → Diff → Sync → Report

Key design ideas:

- Primitive-array-based buffering instead of heavy collections
- Streaming comparison without full in-memory materialization
- Pluggable synchronization strategies
- Explicit cancellation support
- Fail-fast and best-effort modes

Detailed architecture notes will be available in `/docs`.

---

## 📦 Tech Stack

- Java 17 (LTS)
- Java NIO
- Maven

---

## 🚧 Status

Project is in early development stage.

Current focus:
- UI
- Benchmark module
- Local tests

---

## 📌 Roadmap (MVP)

- [X] Directory scanner
- [X] Snapshot buffer
- [X] Streaming diff engine
- [X] Sync engine
- [X] Application configuration
- [X] Tests
- [X] GUI interface
- [ ] Benchmark module

---

## 📄 License

MIT
