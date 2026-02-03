---
apply: always
---

# Git Commit Message Rules

When creating git commit messages, you MUST follow the Udacity Git Commit Message Style Guide:

## Message Structure

All commit messages must have three distinct parts separated by blank lines:
1. **Title** (required): `type: Subject`
2. **Body** (optional): Detailed explanation
3. **Footer** (optional): Issue tracker references

## Commit Types

Use one of these types in the title:

- `feat`: A new feature
- `fix`: A bug fix
- `docs`: Changes to documentation
- `style`: Formatting, missing semi colons, etc; no code change
- `refactor`: Refactoring production code
- `test`: Adding tests, refactoring test; no production code change
- `chore`: Updating build tasks, package manager configs, etc; no production code change

## Subject Line Rules

- Maximum 50 characters
- Begin with a capital letter
- Do NOT end with a period
- Use imperative mood (e.g., "Add feature" not "Added feature" or "Adds feature")

## Body Rules (Optional)

- Separate from title with one blank line
- Wrap lines at 72 characters
- Explain WHAT and WHY, not HOW
- Use bullet points if needed
- Only include if the commit requires explanation and context

## Footer Rules (Optional)

- Use for issue tracker IDs
- Format: `Resolves: #123` or `See also: #456, #789`

## Example Format

```
feat: Add user authentication system

Implement JWT-based authentication to secure API endpoints.
This addresses security concerns raised in the previous audit
and provides a foundation for role-based access control.

- Add JWT token generation and validation
- Create authentication middleware
- Update user model with password hashing

Resolves: #123
```

## Examples by Type

**Feature:**
```
feat: Add password reset functionality
```

**Bug Fix:**
```
fix: Resolve memory leak in image processor
```

**Documentation:**
```
docs: Update API endpoint documentation
```

**Refactoring:**
```
refactor: Simplify user validation logic
```

**Tests:**
```
test: Add unit tests for payment service
```

**Chore:**
```
chore: Update dependencies to latest versions
```
