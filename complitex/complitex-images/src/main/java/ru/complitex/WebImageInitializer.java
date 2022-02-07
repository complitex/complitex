package ru.complitex;

import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.apache.wicket.SharedResources;
import org.apache.wicket.request.resource.PackageResource;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author Artem
 */
public final class WebImageInitializer implements IInitializer {
    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "gif", "bmp", "png");
    private static final String IMAGES_DIRECTORY_NAME = "images";

   /*@Override
    public void init(Application application) {
        try {
            SharedResources sharedResources = application.getSharedResources();
            URI imagesURI = getClass().getResource(IMAGES_DIRECTORY_NAME).toURI();
            File images = new File(imagesURI);

            if (!images.exists()) {
                throw new RuntimeException("Directory " + images.getAbsolutePath() + " doesn't exist.");
            }
            if (!images.isDirectory()) {
                throw new RuntimeException("File " + images.getAbsolutePath() + " is not directory.");
            }

            FilenameFilter imageFilter = new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    return IMAGE_EXTENSIONS.contains(Files.extension(name));
                }
            };

            for (File image : images.listFiles(imageFilter)) {
                String relatedPath = IMAGES_DIRECTORY_NAME + "/" + image.getName();
                //Now resource name is equal to physical related path but it is not required and may will be changed in future.
                String resourceName = relatedPath;
                sharedResources.add(resourceName, new PackageResourceReference(getClass(), relatedPath).getResource());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

  /* @Override
    public void init(Application application) {
        try {
            final SharedResources sharedResources = application.getSharedResources();

            final Map<String, String> env = new HashMap<>();
            final String[] array = WebImageInitializer.class.getResource(IMAGES_DIRECTORY_NAME).toURI().toString().split("!");

            URI uri = URI.create(array[0]);
            FileSystem fs;
            try {
                fs = FileSystems.getFileSystem(uri);
            } catch (Exception e) {
                fs = FileSystems.newFileSystem(uri, env);
            }

            java.nio.file.Files.list(fs.getPath(array[1])).forEach(new Consumer<Path>() {
                @Override
                public void accept(Path path) {
                    String p = IMAGES_DIRECTORY_NAME + "/" + path.getFileName();
                    sharedResources.add(p, new PackageResourceReference(WebImageInitializer.class, p).getResource());
                }
            });

            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void init(Application application) {
        try {
            final SharedResources sharedResources = application.getSharedResources();

            CodeSource src = WebImageInitializer.class.getProtectionDomain().getCodeSource();

            if( src != null ) {
                URL jar = src.getLocation();
                ZipInputStream zip = new ZipInputStream(jar.openStream());
                ZipEntry ze;

                while((ze = zip.getNextEntry() ) != null) {
                    String entryName = ze.getName();
                    if (entryName.contains(".") && entryName.contains("/images")){
                        String p = IMAGES_DIRECTORY_NAME + entryName.substring(entryName.lastIndexOf('/'));
                        sharedResources.add(p, new PackageResource(WebImageInitializer.class, p, null, null, null){});
                    }
                }

                zip.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy(Application application) {
    }
}
