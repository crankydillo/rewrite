Adhoc demo of issue with settings.xml's activeProfile

```sh
mvn -f parent-1/pom.xml install
mvn -f parent-2/pom.xml install
mvn -f recipe/pom.xml install -DskipTests
```

# Does not work

```sh
mvn -e org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.exportDatatables=true -Drewrite.recipeArtifactCoordinates=com.myorg.recipes:recipe-2:0.1.0-SNAPSHOT \
  -Drewrite.activeRecipes=org.openrewrite.FindParseFailures,com.myorg.MyRecipe
```
