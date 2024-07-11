Adhoc demo showing issues changing dependencies.

There are 2 issues exposed here.  

```sh
mvn -f parent-1/pom.xml install
mvn -f parent-2/pom.xml install
mvn -f recipe/pom.xml install -DskipTests
```

```sh
mvn -e org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.exportDatatables=true -Drewrite.recipeArtifactCoordinates=com.myorg.recipes:recipe-2:0.1.0-SNAPSHOT \
  -Drewrite.activeRecipes=org.openrewrite.FindParseFailures,com.myorg.MyRecipe
```
