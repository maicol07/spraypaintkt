package it.maicol07.spraypaintkt_openapi

import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText

/** Generates Spraypaint.Kt resource schemas from a JSON:API-flavoured OpenAPI document. */
object SpraypaintSchemaGenerator {
    /**
     * @param location A local file path or an HTTP(S) URL pointing at the OpenAPI document.
     * @param packageName The package the generated schemas belong to.
     * @param outputDirectory The source root the package directories are created under.
     * @return the written files.
     */
    fun generate(location: String, packageName: String, outputDirectory: Path): List<Path> {
        val (dialect, definitions) = OpenApiResourceParser.parse(location)
        if (definitions.isEmpty()) {
            throw OpenApiSchemaException(
                "No JSON:API resource object found in '$location'. Resource objects are recognised by an " +
                    "`attributes` or `relationships` member, with or without the surrounding `data` envelope."
            )
        }
        // Diagnostics go to stderr so stdout stays a parseable list of written files.
        System.err.println("Read $location as $dialect: ${definitions.joinToString { it.resourceType }}")
        val sources = SchemaFileGenerator(packageName).generate(definitions)
        val packageDirectory = outputDirectory.resolve(packageName.replace('.', '/'))
        packageDirectory.createDirectories()
        return sources.map { (fileName, source) ->
            packageDirectory.resolve(fileName).also { it.writeText(source) }
        }
    }
}

/**
 * CLI entrypoint: `--input <file|url> --package <pkg> --output <dir>`.
 *
 * Existing files are overwritten, so generated schemas stay editable by hand between runs only when
 * they are moved out of the output directory.
 */
fun main(args: Array<String>) {
    val parsed = args.toList().chunked(2)
        .associate { pair ->
            require(pair.size == 2) { "Missing value for option '${pair.first()}'." }
            pair[0].removePrefix("--") to pair[1]
        }
    val input = parsed["input"] ?: error("Missing required option --input <file|url>.")
    val packageName = parsed["package"] ?: error("Missing required option --package <pkg>.")
    val output = parsed["output"] ?: error("Missing required option --output <dir>.")

    val written = SpraypaintSchemaGenerator.generate(input, packageName, Path.of(output))
    val root = Path.of(output).toAbsolutePath()
    written.forEach { println(root.relativize(it.toAbsolutePath())) }
}
