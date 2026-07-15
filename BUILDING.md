# Building from Source

## Prerequisites

- **Java Development Kit (JDK) 21+**
- **Maven 3.8.1+**

## Quick Build

```bash
mvn clean package
```

The compiled JAR will be at: `target/LitematicaPlugin.jar`

## Understanding Maven

Maven is a build tool that:
1. Downloads dependencies (like Paper API)
2. Compiles Java code
3. Packages it into a JAR file

### Key Maven Commands

```bash
mvn clean              # Remove old build files
mvn compile            # Only compile code
mvn test               # Run tests (if any)
mvn package            # Compile and create JAR
mvn clean package      # Clean, then build (safest)
```

### Understanding pom.xml

The `pom.xml` file contains:
- **groupId**: Your organization identifier
- **artifactId**: Project name
- **version**: Project version
- **repositories**: Where to download dependencies
- **dependencies**: Required libraries (Paper API)
- **build/plugins**: Build configuration

## Useful Resources

- Maven Official Docs: https://maven.apache.org/
- Paper Plugin Development: https://docs.papermc.io/
- Java 21 Documentation: https://docs.oracle.com/en/java/javase/21/