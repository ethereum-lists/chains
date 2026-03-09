# Java Installation for Gradle

## Quick Setup

1. **Install Java 21 (recommended for Gradle 8.8):**
   ```bash
   sudo apt update && sudo apt install -y openjdk-21-jdk
   ```

2. **Run the setup script:**
   ```bash
   ./setup-java.sh
   ```

3. **Run Gradle:**
   ```bash
   ./gradlew run
   ```

## Alternative: Java 17

If you prefer Java 17:
```bash
sudo apt update && sudo apt install -y openjdk-17-jdk
./setup-java.sh
```

## Manual Setup

If you prefer to set JAVA_HOME manually:

```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"
```

To make it permanent, add to your `~/.bashrc`:
```bash
echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64' >> ~/.bashrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.bashrc
source ~/.bashrc
```
