import com.google.zxing.BarcodeFormat; // For setting up the barcode format for QR code generation
import com.google.zxing.EncodeHintType; // For setting up hints for QR code generation
import com.google.zxing.WriterException; // For handling exceptions during QR code generation
import com.google.zxing.common.BitMatrix; // For converting QR code matrix to image
import com.google.zxing.qrcode.QRCodeWriter; // For generating QR code matrix

import javax.imageio.ImageIO; // For saving the QR code image to a file
import java.awt.image.BufferedImage; // For converting QR code matrix to image
import java.io.File; // For handling file paths
import java.io.IOException; // For handling exceptions during file operations
import java.util.Scanner; // For taking user input

public class QRCodeGeneratorApp {
    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);

        try (userInput) {
            System.out.println("Enter the data for the QR code:");
            String data = userInput.nextLine();

            System.out.println("Enter the path to save the QR code image:");
            String imagePath = userInput.nextLine();

            // Create a file object for the image path provided by the user
            File imageFile = new File(imagePath);

            // Check if the path is valid
            if (!imageFile.getParentFile().exists()) {
                System.out.println("The directory does not exist!");
            } else if (imageFile.isDirectory()) {
                System.out.println("The provided path is a directory. " +
                        "Going to save the file as `output.png` in the provided directory.");
            } else if (!validatePath(imagePath)) {
                System.out.println("Access denied to file path! Please check the permissions.");
            } else if (!imageFile.getName().endsWith(".png")) {
                System.out.println("The provided path is not a PNG file. " +
                        "Going to save the file as `output.png` in the provided directory.");
            } else if (imageFile.exists() && !imageFile.canWrite()) {
                System.out.println("The file already exists and is not writable. " +
                        "Going to save the file as `output.png` in the provided directory.");
            } else if (imageFile.exists() && imageFile.canWrite()) {
                System.out.println("The file already exists and is writable. " +
                        "Going to overwrite the file.");
            } else if (imageFile.createNewFile()) {
                System.out.println("The file was created successfully. " +
                        "Going to save the file as `output.png` in the provided directory.");
            } else if (!imageFile.createNewFile()) {
                System.out.println("The file was not created successfully. " +
                        "Please check the permissions.");
            } else if (data.isEmpty() || imagePath.isEmpty()) { // Check if the data or path is empty
                System.out.println("The data or path is empty.");
            } else if (!imagePath.endsWith(File.separator)) {
                imagePath += File.separator + "output.png"; // Append the file name to the path
            } else {
                generateQRCode(data, imagePath); // Generate QR code and save it to a file
                System.out.println("QR code generated successfully!");
            }
            String filePath = imagePath + "output.png"; // Append the file name to the path
            generateQRCode(data, filePath); // Generate QR code and save it to a file
        } catch (WriterException | IOException e) {
            System.err.println("Error generating QR code: " + e.getMessage());
        }
    }

    // Helper method to validate the file path provided by the user
    private static boolean validatePath(String path) {
        File file = new File(path); // Create a file object for the path provided by the user

        // Check if the parent directory exists and is writable for the current user
        return file.getParentFile().exists() && file.getParentFile().canWrite();
    }

    // Helper method to generate QR code and save it to a file
    private static void generateQRCode(String data, String imagePath)
            throws WriterException, IOException {

        QRCodeWriter qrCodeWriter = new QRCodeWriter(); // Create a QR code writer object

        // Set up hints for QR code generation (optional)
        java.util.Map<EncodeHintType, Object> hints = new java.util.HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // Set character encoding

        // Generate QR code matrix from the data using the QR code writer object
        BitMatrix bitMatrix = qrCodeWriter.encode(data,
                BarcodeFormat.QR_CODE, 200, 200, hints); // Set QR code size to 200x200 pixels

        // Convert matrix to image and save it to a file using helper methods
        BufferedImage qrCodeImage = toBufferedImage(bitMatrix);
        saveImage(qrCodeImage, imagePath); // Save the image to a file using the provided path
    }

    // Helper method to convert BitMatrix to BufferedImage for saving to a file
    private static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth(); // Get the width of the QR code matrix
        int height = matrix.getHeight(); // Get the height of the QR code matrix

        // Create a BufferedImage object with the width and height of the QR code matrix
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // Set the pixels of the image to black or white based on the QR code matrix

        // Iterate over the QR code matrix width and height to set the pixels of the image
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                // Set the pixels of the image to black or white based on the QR code matrix
                image.setRGB(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                // 0xFF000000 is black and 0xFFFFFFFF is white in RGB color format
            }
        }
        return image; // Return the BufferedImage object
    }

    // Helper method to save BufferedImage to a file
    private static void saveImage(BufferedImage image, String filePath) throws IOException {
        File file = new File(filePath); // Create a file object for the file path provided by the user

        // Check if the parent directory exists and is writable for the current user
        ImageIO.write(image, "png", file); // Write the image to a file using the provided path
    }
}
