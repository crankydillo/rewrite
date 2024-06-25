Adhoc demo of issue with settings.xml's activeProfile

```sh
mvn -f parent/pom.xml install
mvn -f recipe/pom.xml install
```

# Does not work (active profile) 

```sh
mvn -s does-not-work-settings.xml -e org.openrewrite.maven:rewrite-maven-plugin:run \
    -Drewrite.exportDatatables=true -Drewrite.recipeArtifactCoordinates=com.myorg.recipes:recipe:0.1.0-SNAPSHOT \
    -Drewrite.activeRecipes=org.openrewrite.FindParseFailures,com.myorg.MyRecipe
```

# Works (no active profile)

```sh
mvn -s works-settings.xml -e org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.exportDatatables=true -Drewrite.recipeArtifactCoordinates=com.myorg.recipes:recipe:0.1.0-SNAPSHOT \
  -Drewrite.activeRecipes=org.openrewrite.FindParseFailures,com.myorg.MyRecipe
```
