# MongoMovies - Security & Setup Guide

## TMDB API Key Setup

### 1. Get Your API Key
1. Go to https://www.themoviedb.org/
2. Create an account or sign in
3. Navigate to Settings → API
4. Request an API key (free for non-commercial use)
5. Copy your API key (v3 auth)

### 2. Add to local.properties
Create or edit `local.properties` in the project root:

```properties
# TMDB API Key - NEVER commit this file
TMDB_API_KEY=your_api_key_here
```

### 3. Verify Setup
Build the app and check logs:
```bash
./gradlew assembleDebug
adb logcat | grep AppContainer
```

You should see:
```
I/AppContainer: TMDB API Key present: true
```

---

## Security Best Practices

### ✅ DO
- Store API key in `local.properties`
- Use BuildConfig for injection
- Log only boolean presence (true/false)
- Use DefaultRequest plugin for automatic injection
- Handle TMDB error responses safely

### ❌ DON'T
- Hard-code API keys in source code
- Commit `local.properties` to git
- Log the actual API key value
- Manually add api_key to each request
- Ignore TMDB error responses

---

## Why local.properties?

1. **Automatically Gitignored**: Android Studio gitignores this file by default
2. **Per-Developer Keys**: Each developer uses their own API key
3. **CI/CD Integration**: Production keys injected via environment variables
4. **Security**: Prevents unauthorized API usage and quota theft
5. **Compliance**: Meets TMDB Terms of Service requirements

---

## File Security Status

| File | Contains Key? | Committed? | Status |
|------|---------------|------------|--------|
| `local.properties` | ✅ Yes | ❌ No (gitignored) | ✅ Secure |
| `BuildConfig.java` | ✅ Yes (generated) | ❌ No (build output) | ✅ Secure |
| Source code | ❌ No | ✅ Yes | ✅ Secure |
| Version control | ❌ No | N/A | ✅ Secure |

---

## Troubleshooting

### "TMDB API Key present: false"
**Solution:** Add `TMDB_API_KEY=your_key` to `local.properties`

### "Invalid API key" error
**Solution:** Verify your API key is correct and active on TMDB

### Movies not loading
**Solution:** Check logcat for error messages and verify API key setup

---

## Production Deployment

For production builds, inject the API key via CI/CD:

```bash
# GitHub Actions example
echo "TMDB_API_KEY=${{ secrets.TMDB_API_KEY }}" >> local.properties
./gradlew assembleRelease
```

**Never commit production keys to version control!**
