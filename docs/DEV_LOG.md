# AI Note Cleaner - Development Log

A record of development sessions, decisions made, problems solved, and lessons learned.

---

## How to Use This Log

After each development session, add an entry with:
- **Date and session duration**
- **What you worked on**
- **Decisions made and why**
- **Problems encountered and solutions**
- **AI prompts that worked well (or didn't)**
- **What you learned**
- **Next steps**

This becomes interview material and helps you remember your thought process.

---

## Log Entries

### 2024-12-23 - Project Planning & Architecture

**Duration:** ~2 hours  
**Phase:** Pre-development

**What I Did:**
- Reviewed AI-native engineering workflow
- Created project documentation structure
- Defined Phase 1 scope and acceptance criteria
- Made architecture decisions (documented in ARCHITECTURE.md)

**Key Decisions Made:**

1. **Backend-first approach** - Build API before UI because frontend depends on backend, not vice versa. Lets me test core logic before adding visual complexity.

2. **Placeholder before AI** - Implement simple string manipulation first, then swap in AI. This separates "does the API work" from "does the AI work."

3. **Layered package structure** - Organizing by layer (controller/, service/, dto/) rather than by feature. Standard Spring convention, easier for others to understand.

**Problems Encountered:**
- None yet - still in planning phase

**AI Tool Usage:**
- Used Claude to understand architecture decision framework
- Created INSTRUCTIONS.md template for guiding OpenHands

**What I Learned:**
- The importance of asking "what depends on what" when deciding build order
- How to structure instructions for AI coding tools
- ADR (Architecture Decision Record) format for documenting choices

**Next Session:**
- Create the docs/ folder in actual repo
- Add INSTRUCTIONS.md, ARCHITECTURE.md, DEV_LOG.md
- Start Phase 1 with OpenHands: begin with DTOs

---

### Template for Future Entries

```markdown
### YYYY-MM-DD - [Session Title]

**Duration:** X hours  
**Phase:** [1/2/3/4]

**What I Did:**
- 

**Commits Made:**
- `type: description` - [link if applicable]

**Problems Encountered:**
- Problem:
- Solution:
- Time spent:

**AI Prompts That Worked:**
```
[paste effective prompts here]
```

**AI Prompts That Didn't Work:**
```
[paste ineffective prompts and why]
```

**What I Learned:**
- 

**Code I Want to Remember:**
```java
// paste notable code snippets
```

**Questions for Later:**
- 

**Next Session:**
- 
```

---

## Running Notes

### Useful Commands

```bash
# Backend
cd backend
./mvnw spring-boot:run          # Start server
./mvnw test                      # Run tests
./mvnw clean package             # Build JAR

# Frontend
cd frontend
npm run dev                      # Start dev server
npm run build                    # Production build
npm run lint                     # Check code quality

# Testing API
curl -X POST http://localhost:8080/api/notes/clean \
  -H "Content-Type: application/json" \
  -d '{"content": "Test content.", "outputFormat": "bullets"}'

# Git
git status
git add .
git commit -m "type: description"
git push origin main
```

### Things I Keep Forgetting

- [ ] (Add items here as you encounter them)

### Interview Talking Points

Build this list as you develop - specific examples for behavioral interviews:

**Technical Decisions:**
- "I chose backend-first because..."

**Problem Solving:**
- (Add as you solve problems)

**AI-Native Workflow:**
- "I structured my prompts by..."
- "When the AI gave me code I didn't understand, I..."

---

## Metrics (Optional)

Track if you're curious about your development patterns:

| Date | Hours | Lines Added | Tests Written | AI Prompts | Commits |
|------|-------|-------------|---------------|------------|---------|
| 2024-12-23 | 2 | 0 | 0 | 5 | 0 |

---

*This log is for YOU. Be honest about struggles - that's where learning happens.*
