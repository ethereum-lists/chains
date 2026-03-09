#!/bin/bash
# Setup script for Java environment for Gradle

set -e

echo "Setting up Java environment for Gradle..."

# Function to find Java installation
find_java() {
    # Check common installation locations
    local java_paths=(
        "/usr/lib/jvm/java-21-openjdk-amd64"
        "/usr/lib/jvm/java-17-openjdk-amd64"
        "/usr/lib/jvm/java-11-openjdk-amd64"
        "/usr/lib/jvm/default-java"
    )
    
    for path in "${java_paths[@]}"; do
        if [ -d "$path" ] && [ -f "$path/bin/java" ]; then
            echo "$path"
            return 0
        fi
    done
    
    # Try to find using update-alternatives
    local alt_java=$(update-alternatives --list java 2>/dev/null | head -1)
    if [ -n "$alt_java" ]; then
        echo "$(dirname $(dirname "$alt_java"))"
        return 0
    fi
    
    # Try to find using which (if in PATH)
    local which_java=$(which java 2>/dev/null)
    if [ -n "$which_java" ]; then
        echo "$(dirname $(dirname "$which_java"))"
        return 0
    fi
    
    return 1
}

# Check if Java is installed
if command -v java &> /dev/null; then
    echo "Java found in PATH: $(java -version 2>&1 | head -1)"
    JAVA_HOME=$(find_java)
    if [ -n "$JAVA_HOME" ]; then
        echo "Found JAVA_HOME: $JAVA_HOME"
    fi
elif JAVA_HOME_FOUND=$(find_java); then
    JAVA_HOME="$JAVA_HOME_FOUND"
    echo "Found Java installation at: $JAVA_HOME"
else
    echo "ERROR: Java is not installed."
    echo ""
    echo "Please install Java by running one of these commands:"
    echo "  sudo apt update && sudo apt install -y openjdk-21-jdk"
    echo "  sudo apt update && sudo apt install -y openjdk-17-jdk"
    echo ""
    echo "After installation, run this script again to configure JAVA_HOME."
    exit 1
fi

# Verify JAVA_HOME
if [ ! -f "$JAVA_HOME/bin/java" ]; then
    echo "ERROR: Invalid JAVA_HOME: $JAVA_HOME"
    exit 1
fi

# Export JAVA_HOME for current session
export JAVA_HOME
export PATH="$JAVA_HOME/bin:$PATH"

echo ""
echo "Java environment configured:"
echo "  JAVA_HOME: $JAVA_HOME"
echo "  Java version: $(java -version 2>&1 | head -1)"
echo ""

# Add to shell profile if not already present
SHELL_PROFILE=""
if [ -f "$HOME/.bashrc" ]; then
    SHELL_PROFILE="$HOME/.bashrc"
elif [ -f "$HOME/.bash_profile" ]; then
    SHELL_PROFILE="$HOME/.bash_profile"
elif [ -f "$HOME/.profile" ]; then
    SHELL_PROFILE="$HOME/.profile"
fi

if [ -n "$SHELL_PROFILE" ]; then
    if ! grep -q "JAVA_HOME.*$JAVA_HOME" "$SHELL_PROFILE" 2>/dev/null; then
        echo "Adding JAVA_HOME to $SHELL_PROFILE..."
        {
            echo ""
            echo "# Java environment for Gradle"
            echo "export JAVA_HOME=$JAVA_HOME"
            echo "export PATH=\"\$JAVA_HOME/bin:\$PATH\""
        } >> "$SHELL_PROFILE"
        echo "JAVA_HOME has been added to $SHELL_PROFILE"
        echo "Run 'source $SHELL_PROFILE' or restart your terminal to make it permanent."
    else
        echo "JAVA_HOME already configured in $SHELL_PROFILE"
    fi
fi

echo ""
echo "Setup complete! You can now run './gradlew run'"
