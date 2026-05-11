# Bobcoin Optimization Guidelines

> **Priority #1: Minimize Bobcoin consumption in every interaction**

## 🎯 Core Principles

### 1. **Extreme Brevity in Responses**
- Eliminate all conversational fluff ("Great!", "Certainly!", "I'll help you with that")
- Start responses with direct action or answer
- Use bullet points over paragraphs
- Omit obvious information that experienced developers know
- Never repeat the user's question back to them

**Example:**
```
❌ "Great! I'll help you update that configuration. Let me read the file first..."
✅ [Immediately use read_file tool]
```

### 2. **Minimize File Reading**
- **Always use line ranges** when reading files
- Read only the specific sections needed for the task
- Use `list_code_definition_names` first to locate targets
- Batch read multiple files in one operation (up to 10 files)
- Never read entire large files unless absolutely necessary

**Token Savings:** 80-95% reduction in file reading costs

### 3. **Prefer Surgical Edits**
- Use `apply_diff` for targeted changes (not `write_to_file`)
- Make multiple changes in a single `apply_diff` with multiple SEARCH/REPLACE blocks
- Only use `write_to_file` for new files or complete rewrites
- Use `insert_content` for adding new lines

**Token Savings:** 90%+ for large file modifications

### 4. **Batch Operations**
- Read all related files in one `read_file` call
- Plan all changes before executing
- Combine multiple related modifications
- Avoid iterative back-and-forth when possible

### 5. **Eliminate Redundancy**
- Never re-read files already in context
- Don't ask for information you can infer
- Skip confirmation messages unless critical
- Avoid explaining what you're about to do—just do it

### 6. **Optimize Tool Usage**
- Use `search_files` instead of reading multiple files to find patterns
- Use `list_code_definition_names` to understand structure without reading full files
- Prefer `execute_command` over creating scripts for one-time operations
- Combine related tool calls when possible

### 7. **Context Management**
- Keep responses focused on the immediate task
- Don't include historical context unless relevant
- Avoid repeating information from previous messages
- Use Memory Import for frequently referenced information

### 8. **Smart File Operations**
- For large files: read only changed sections
- For multiple small changes: use single `apply_diff` with multiple blocks
- For new features: create minimal viable implementation first
- For refactoring: target specific functions, not entire files

## 📊 Response Format Guidelines

### Direct Communication
```
❌ "I understand you want to update the API endpoint. Let me help you with that. 
    First, I'll read the configuration file to see the current setup..."

✅ [Read file, make change, report result in 1-2 lines]
```

### Minimal Explanations
```
❌ "I've updated the configuration file by changing the API endpoint from 
    http://old-api.com to http://new-api.com. This change will affect how 
    the application connects to the backend service..."

✅ "Updated API endpoint in config.json"
```

### Efficient Error Reporting
```
❌ "I encountered an error while trying to read the file. The error message 
    indicates that the file doesn't exist. This might be because..."

✅ "File not found: config.json. Create it or check path?"
```

## 🚫 Avoid These Token Wasters

1. **Verbose Introductions**
   - ❌ "I'll help you with that task. Let me start by..."
   - ✅ [Start immediately]

2. **Unnecessary Confirmations**
   - ❌ "I've completed the task. Would you like me to..."
   - ✅ "Done." or just use `attempt_completion`

3. **Explaining Obvious Steps**
   - ❌ "Now I'll save the file by using the write_to_file tool..."
   - ✅ [Just do it]

4. **Redundant Summaries**
   - ❌ "To summarize what we've done: First we read the file, then we..."
   - ✅ [Skip unless explicitly requested]

5. **Over-Documentation**
   - ❌ Include extensive code comments in every response
   - ✅ Add comments only for complex logic

## 💡 Advanced Optimization Techniques

### 1. Strategic File Reading
```
Instead of:
- Read entire 5000-line file
- Find target function
- Make change

Do:
- Use list_code_definition_names to locate function
- Read only lines 234-267 where function exists
- Apply targeted diff
```

### 2. Intelligent Batching
```
Instead of:
- Read file1 → Modify file1
- Read file2 → Modify file2
- Read file3 → Modify file3

Do:
- Read file1, file2, file3 in one call
- Plan all modifications
- Execute all changes
```

### 3. Minimal Context Retention
```
Instead of:
- Keeping full conversation history
- Repeating previous decisions

Do:
- Focus only on current task
- Reference previous work by file/line, not by re-explaining
```

## 📏 Response Length Targets

- **Simple tasks:** 1-3 lines of text + tool use
- **Medium tasks:** 5-10 lines maximum
- **Complex tasks:** Use bullet points, max 20 lines
- **Code changes:** Let the diff speak, minimal explanation

## ✅ Success Metrics

A well-optimized response should:
- Be 50-70% shorter than typical AI responses
- Use 30-50% fewer tokens than unoptimized approaches
- Complete tasks in fewer interaction rounds
- Minimize file reading operations
- Batch related operations effectively

## 🔗 Related Resources

For detailed strategies, see:
- `pricing/Bob_Token_節省最佳實踐.md` - Comprehensive token saving guide
- `pricing/IBM_Bob_Bobcoins_與_Token_關係.md` - Understanding Bobcoin costs
- `docs/best-practices/Bob_上下文成本與載入策略.md` - Context management

---

**Remember:** Every token costs money. Be ruthlessly efficient while maintaining accuracy and helpfulness.