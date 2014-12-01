package org.complitex;

import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.apache.wicket.SharedResources;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.file.Files;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 * @author Artem
 */
public final class WebImageInitializer implements IInitializer {
    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "gif", "bmp", "png");
    private static final String IMAGES_DIRECTORY_NAME = "images";

   @Override
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
    }

   /* @Override
    public void init(Application application) {
        try {
            final SharedResources sharedResources = application.getSharedResources();

            final Map<String, String> env = new HashMap<>();
            final String[] array = getClass().getResource(IMAGES_DIRECTORY_NAME).toURI().toString().split("!");

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
    public void destroy(Application application) {
    }
}
