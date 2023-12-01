import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InstallPDV {

    public static void main(String[] args) {
        // Diretório de instalação (C:\PDVUNIGEX)
        String installPath = "C:\\PDVUNIGEX";

        // Crie o diretório de instalação se não existir
        try {
            Files.createDirectories(Paths.get(installPath));
        } catch (IOException e) {
            handleError("Erro ao criar o diretório de instalação", e);
            return;
        }

        // Copie a pasta UNIGEXPDV para o diretório de instalação
        try {
            copyFolderFromResources("/UnigexPDV", installPath);
            System.out.println("Pasta UnigexPDV copiada com sucesso para " + installPath);
        } catch (IOException | URISyntaxException e) {
            handleError("Erro ao copiar a pasta UnigexPDV", e);
        }

        System.out.println("Instalação concluída com sucesso.");
    }

    private static void copyFolderFromResources(String source, String destination) throws IOException, URISyntaxException {
        URI sourceUri = Objects.requireNonNull(InstallPDV.class.getResource(source)).toURI();
        Map<String, String> env = new HashMap<>();

        // Verificar se o recurso está dentro de um JAR
        if (sourceUri.getScheme().equals("jar")) {
            try (FileSystem fileSystem = FileSystems.newFileSystem(sourceUri, env)) {
                Path sourcePath = fileSystem.getPath(source);
                Path destPath = Paths.get(destination);

                Files.walk(sourcePath)
                        .forEach(sourceFilePath -> {
                            try {
                                Path relativePath = sourcePath.relativize(sourceFilePath);
                                Path destFilePath = destPath.resolve(relativePath);

                                if (Files.isDirectory(sourceFilePath)) {
                                    Files.createDirectories(destFilePath);
                                } else {
                                    Files.copy(sourceFilePath, destFilePath, StandardCopyOption.REPLACE_EXISTING);
                                }
                            } catch (IOException e) {
                                handleError("Erro ao copiar o arquivo/pasta", e);
                            }
                        });
            }
        } else {
            // Recurso não está dentro de um JAR
            Path sourcePath = Paths.get(sourceUri);
            Path destPath = Paths.get(destination);

            Files.walk(sourcePath)
                    .forEach(sourceFilePath -> {
                        try {
                            Path relativePath = sourcePath.relativize(sourceFilePath);
                            Path destFilePath = destPath.resolve(relativePath);

                            if (Files.isDirectory(sourceFilePath)) {
                                Files.createDirectories(destFilePath);
                            } else {
                                Files.copy(sourceFilePath, destFilePath, StandardCopyOption.REPLACE_EXISTING);
                            }
                        } catch (IOException e) {
                            handleError("Erro ao copiar o arquivo/pasta", e);
                        }
                    });
        }
    }

    private static void handleError(String message, Exception e) {
        System.out.println(message + ": " + e.getMessage());
        e.printStackTrace();  // Isso pode ser ajustado conforme necessário para o seu ambiente.
    }
}
