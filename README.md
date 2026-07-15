# LitematicaPlugin

Schematic auto-download plugin for Paper 1.21+ Minecraft servers.

## Quick Start

### Step 1: Install Java 21 and Maven

**On Windows:**
1. Download Java 21 from: https://www.oracle.com/java/technologies/downloads/#java21
2. Run installer (choose default options)
3. Download Maven from: https://maven.apache.org/download.cgi
4. Extract to `C:\Apache\maven` (or your preferred location)
5. Add Maven to PATH:
   - Right-click "This PC" → Properties
   - Click "Advanced system settings"
   - Click "Environment Variables"
   - Click "New" under System variables
   - Variable name: `MAVEN_HOME`
   - Variable value: `C:\Apache\maven` (your installation path)
   - Click OK
   - Find "Path" in System variables, click Edit
   - Click "New" and add: `%MAVEN_HOME%\bin`
   - Click OK and close
6. Open Command Prompt and type: `mvn --version` (should show version)

**On Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install openjdk-21-jdk maven
mvn --version  # Verify installation
```

**On macOS:**
```bash
brew install openjdk@21 maven
mvn --version  # Verify installation
```

### Step 2: Clone and Build

```bash
# Clone the repository
git clone https://github.com/ILikeRedstone-ai/LitematicaPlugin.git
cd LitematicaPlugin

# Build with Maven
mvn clean package

# JAR file created at: target/LitematicaPlugin.jar
```

### Step 3: Add to Server

1. Copy `target/LitematicaPlugin.jar` to your server's `/plugins/` folder
2. Create your test schematic file location:
   ```bash
   mkdir -p /plugins/LitematicaPlugin/schematics/
   cp /path/to/your/test.litematica /plugins/LitematicaPlugin/schematics/
   ```
3. Restart your Paper server
4. Check console for:
   ```
   [LitematicaPlugin] LitematicaPlugin enabled!
   [LitematicaPlugin] Schematics folder: ...
   ```

### Step 4: Configuration

Edit `/plugins/LitematicaPlugin/config.yml`:

```yaml
test-schematic: "test.litematica"   # Your test file name
prefix: "Tested - "                 # Prefix added to schematic
debug: true                          # Show debug messages
```

**No recompilation needed!** Just restart the server.

### Step 5: Test the Plugin

In-game as OP:
```
/test
```

You should see:
```
§aSchematic sent! Check your Litematica schematics folder.
```

## File Structure

```
LitematicaPlugin/
├── pom.xml                          # Maven build config
├── src/
│   └── main/
│       ├── java/com/litematics/plugin/
│       │   ├── LitematicaPlugin.java        # Main plugin class
│       │   ├── command/
│       │   │   └── TestCommand.java         # /test command
│       │   └── manager/
│       │       └── SchematicManager.java    # File handling
│       └── resources/
│           ├── plugin.yml                   # Plugin config
│           └── config.yml                   # User settings
└── target/
    └── LitematicaPlugin.jar         # Final JAR (after building)
```

## Troubleshooting

### "mvn command not found"
- Maven is not in PATH
- Restart Command Prompt after adding to PATH
- Or use full path: `C:\Apache\maven\bin\mvn --version`

### "Could not find or load main class"
- Ensure you're using Java 21+
- Run: `java -version`

### Plugin doesn't load
- Check server console for errors
- Verify `/plugins/LitematicaPlugin/` folder exists
- Check `config.yml` syntax (YAML is whitespace-sensitive)

### Command doesn't work
- Check file exists: `/plugins/LitematicaPlugin/schematics/test.litematica`
- Check debug messages in console
- Ensure you have OP permissions

## Next Steps

Once `/test` works, we'll add:
1. Packet sending to client
2. Security validations
3. Multiple schematic support
4. More commands

## License

MIT License