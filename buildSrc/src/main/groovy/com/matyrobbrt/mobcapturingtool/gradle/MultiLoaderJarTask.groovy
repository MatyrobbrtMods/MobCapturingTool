package com.matyrobbrt.mobcapturingtool.gradle

import groovy.json.JsonGenerator
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.jar.Attributes
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.jar.Manifest
import java.util.zip.ZipEntry

@CompileStatic
abstract class MultiLoaderJarTask extends DefaultTask {
    @Internal
    final Manifest manifest = new Manifest()

    MultiLoaderJarTask() {
        this.output.convention(project.layout.file(project.provider {
            new File(project.buildDir, 'libs/' + archiveIdentifier.get() + "-" + archiveVersion.get() + "-ml.jar")
        }))
    }

    @InputFile
    abstract RegularFileProperty getForgeJar()
    @InputFile
    abstract RegularFileProperty getFabricJar()

    @OutputFile
    abstract RegularFileProperty getOutput()

    @Input
    abstract Property<String> getArchiveGroup()
    @Input
    abstract Property<String> getArchiveVersion()
    @Input
    abstract Property<String> getArchiveIdentifier()

    @TaskAction
    void exec() {
        final out = output.get().asFile.toPath()
        Files.createDirectories(out.parent)
        Files.deleteIfExists(out)
        manifest.mainAttributes.put(Attributes.Name.MANIFEST_VERSION, '1.0')
        try (final JarOutputStream jarOut = new JarOutputStream(Files.newOutputStream(out), manifest)) {
            {
                final forgeJar = this.forgeJar.asFile.get().toPath()
                final jarPath = 'META-INF/jarjar/' + forgeJar.getFileName().toString()
                jarOut.putNextEntry(new ZipEntry(jarPath))
                Files.newInputStream(forgeJar).withCloseable {
                    it.transferTo(jarOut)
                }
                jarOut.closeEntry()

                jarOut.putNextEntry(new ZipEntry('META-INF/jarjar/metadata.json'))
                jarOut.write(new JsonGenerator.Options()
                    .build().toJson([
                            jars: [[
                                    identifier: [
                                            group: this.archiveGroup.get(),
                                            artifact: this.archiveIdentifier.get()
                                    ],
                                    version: [
                                            artifactVersion: this.archiveVersion.get(),
                                            range: "[${this.archiveVersion.get()}]"
                                    ],
                                    path: jarPath,
                                    isObfuscated: true
                            ]]
                    ]).getBytes(StandardCharsets.UTF_8))
                jarOut.closeEntry()
            }

            {
                final fabricJar = this.fabricJar.asFile.get().toPath()
                final jarPath = 'META-INF/jars/' + fabricJar.getFileName().toString()
                jarOut.putNextEntry(new ZipEntry(jarPath))
                Files.newInputStream(fabricJar).withCloseable {
                    it.transferTo(jarOut)
                }
                jarOut.closeEntry()

                jarOut.putNextEntry(new ZipEntry('fabric.mod.json'))
                jarOut.write(new JsonGenerator.Options()
                    .build().toJson([
                        schemaVersion: 1,
                        id: archiveIdentifier.get() + '_mljar',
                        version: archiveVersion.get(),
                        name: archiveIdentifier.get(),
                        jars: [[
                                file: jarPath
                        ]]
                    ]).getBytes(StandardCharsets.UTF_8))
                jarOut.closeEntry()
            }
        }
    }
}
