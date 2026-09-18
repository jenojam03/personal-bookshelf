package persistence;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Libro;


import java.io.File;
import java.io.IOException;
import java.util.*;

public class JsonStorageManager {
    //private static File file = new File(ConfigManager.caricaPercorso());
    private static final ObjectMapper mapper = new ObjectMapper();

    private static File getFile(){
        return new File(ConfigManager.caricaPercorso());
    }


    // Carica da JSON e restituisce una mappa ISBN -> Libro
    public static Map<String, Libro> caricamento() throws IOException {
        File file = getFile();
        if (!file.exists()){
            return new LinkedHashMap<>();
        }


        Map<String, Libro> libri = mapper.readValue(file, new TypeReference<LinkedHashMap<String, Libro>>() {});

        return libri;
    }

    // Salva una mappa ISBN -> Libro
    public static void salvataggio(Map<String, Libro> libri) throws IOException {
        File file = getFile();
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, libri);
    }
}
