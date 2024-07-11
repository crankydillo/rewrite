import org.junit.jupiter.api.Test;
import org.openrewrite.ExecutionContext;
import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.Parser;
import org.openrewrite.config.Environment;
import org.openrewrite.java.Assertions;
import org.openrewrite.java.JavaParser;
import org.openrewrite.maven.MavenExecutionContextView;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;
import org.openrewrite.test.SourceSpec;

import static org.openrewrite.java.Assertions.mavenProject;
import static org.openrewrite.maven.Assertions.pomXml;

class RecipeTest implements RewriteTest {

    @Override
    public void defaults(final RecipeSpec spec) {

        spec.recipe(Environment.builder()
                .scanYamlResources()
                .build()
                .activateRecipes("com.myorg.MyRecipe")
        );
    }

    /*
    @Override
    public ExecutionContext defaultExecutionContext(final SourceSpec<?>[] sourceSpecs) {
        return MavenExecutionContextView.view(RewriteTest.super.defaultExecutionContext(sourceSpecs));
    }
     */

    @Test
    void recipeTest() {
        rewriteRun(
                // How can I leverage these 2 parent version without them being affected by the
                // recipe being tested???  We really want a final step to remove the old commons-collection
                // dependency, but it's affecting parent-1.0.0, probably harmless, but I don't want to do
                // that...
                mavenProject("parent", pomXml("""
                        <project>
                            <modelVersion>4.0.0</modelVersion>
                            <groupId>com.myorg.parent</groupId>
                            <artifactId>parent</artifactId>
                            <version>1.0.0</version>
                            <packaging>pom</packaging>
                            <dependencyManagement>
                                <dependencies>
                                    <dependency>
                                        <groupId>commons-collections</groupId>
                                        <artifactId>commons-collections</artifactId>
                                        <version>3.2.2</version>
                                    </dependency>
                                </dependencies>
                            </dependencyManagement>
                        </project>
                        """, """
                        <project>
                            <modelVersion>4.0.0</modelVersion>
                            <groupId>com.myorg.parent</groupId>
                            <artifactId>parent</artifactId>
                            <version>1.0.0</version>
                            <packaging>pom</packaging>
                        </project>
                        """)
                ), mavenProject("parent2", pomXml("""
                        <project>
                            <modelVersion>4.0.0</modelVersion>
                            <groupId>com.myorg.parent</groupId>
                            <artifactId>parent</artifactId>
                            <version>2.0.0</version>
                            <packaging>pom</packaging>
                            <dependencyManagement>
                                <dependencies>
                                    <dependency>
                                        <groupId>org.apache.commons</groupId>
                                        <artifactId>commons-collections4</artifactId>
                                        <version>4.4</version>
                                    </dependency>
                                </dependencies>
                            </dependencyManagement>
                        </project>
                        """)
                ), mavenProject("app-parent", pomXml(
                                """
                                        <project>
                                            <modelVersion>4.0.0</modelVersion>
                                            <parent>
                                                <groupId>com.myorg.parent</groupId>
                                                <artifactId>parent</artifactId>
                                                <version>1.0.0</version>
                                            </parent>
                                            <groupId>com.myorg.app</groupId>
                                            <artifactId>profile-test-2</artifactId>
                                            <version>5.0.0-SNAPSHOT</version>
                                            <packaging>pom</packaging>
                                            <modules>
                                                <module>module-1</module>
                                            </modules>
                                            <dependencies>
                                                <dependency>
                                                    <groupId>commons-collections</groupId>
                                                    <artifactId>commons-collections</artifactId>
                                                </dependency>
                                            </dependencies>
                                        </project>
                                            """,
                                """
                                        <project>
                                            <modelVersion>4.0.0</modelVersion>
                                            <parent>
                                                <groupId>com.myorg.parent</groupId>
                                                <artifactId>parent</artifactId>
                                                <version>2.0.0</version>
                                            </parent>
                                            <groupId>com.myorg.app</groupId>
                                            <artifactId>profile-test-2</artifactId>
                                            <version>5.0.0-SNAPSHOT</version>
                                            <packaging>pom</packaging>
                                            <modules>
                                                <module>module-1</module>
                                            </modules>
                                            <dependencies>
                                                <dependency>
                                                    <groupId>org.apache.commons</groupId>
                                                    <artifactId>commons-collections4</artifactId>
                                                </dependency>
                                            </dependencies>
                                        </project>       
                                            """
                        )), mavenProject("child", pomXml(
                                """
                                        <project>
                                            <modelVersion>4.0.0</modelVersion>
                                            <parent>
                                                <groupId>com.myorg.app</groupId>
                                                <artifactId>profile-test-2</artifactId>
                                                <version>5.0.0-SNAPSHOT</version>
                                            </parent>
                                            <artifactId>module-1</artifactId>
                                            <dependencies>
                                                <dependency>
                                                    <groupId>commons-collections</groupId>
                                                    <artifactId>commons-collections</artifactId>
                                                </dependency>
                                            </dependencies>
                                        </project>
                                        """, """
                                        <project>
                                            <modelVersion>4.0.0</modelVersion>
                                            <parent>
                                                <groupId>com.myorg.app</groupId>
                                                <artifactId>profile-test-2</artifactId>
                                                <version>5.0.0-SNAPSHOT</version>
                                            </parent>
                                            <artifactId>module-1</artifactId>
                                            <dependencies>
                                                <dependency>
                                                    <groupId>org.apache.commons</groupId>
                                                    <artifactId>commons-collections4</artifactId>
                                                </dependency>
                                            </dependencies>
                                        </project>
                                                """
                        )
                )
        );
    }
}
