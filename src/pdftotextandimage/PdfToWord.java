package pdftotextandimage;

import java.awt.FileDialog;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;







public class PdfToWord extends PDFTextStripper
{
  private static final String OUTPUT_DIR = "\\";
  public static JSONObject obj = new JSONObject();
  public static JSONArray images = new JSONArray();
  public static JSONArray text = new JSONArray();
  
  public static JProgressBar pbar;
  
  public static JFrame frame;
  

  public PdfToWord()
    throws IOException
  {}
  

  public static void main(String[] args)
    throws IOException
  {
	// Setting up OS UI
	try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }catch(Exception ex) {
        ex.printStackTrace();
    }
	
	final JFrame mainFrame = new JFrame();
	mainFrame.setSize(200, 200);
	mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	FileDialog fd = new FileDialog(mainFrame, "Select PDF", FileDialog.LOAD);
	fd.setVisible(true);
	String filePath = "";
	if(fd.getFile() != null) {
		filePath = fd.getDirectory().toString();
		System.out.println(filePath);
		PDDocument document = null;
	    convertPdfToTextAndImage(document, filePath);
	    System.gc();
	    System.exit(1);
    }else {
    	fd.setVisible(false);
    	System.gc();
	    System.exit(1);
    }
	  
	  
//    JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getDefaultDirectory());
//    String fileName = "";
//    
//    fileChooser.setName("Open PDF File");
//    
//    int r = fileChooser.showOpenDialog(null);
//    
//    if (r == 0) {
//      fileName = fileChooser.getSelectedFile().getAbsolutePath();
//    } else {
//      fileChooser.setVisible(false);
//    }
    
    
  }
  
  private static void convertPdfToTextAndImage(PDDocument doc, String file_name) throws IOException
  {
    if (file_name != null) {
      showProgressBar();
      doc = PDDocument.load(new File(file_name));
      doc.getClass();
      if (!doc.isEncrypted()) {
        PDFTextStripperByArea txtStripper = new PDFTextStripperByArea();
        txtStripper.setSortByPosition(true);
        PDFTextStripper Tstripper = new PDFTextStripper();
        Tstripper.setSortByPosition(true);
        Tstripper.setStartPage(0);
        Tstripper.setEndPage(doc.getNumberOfPages());
        String st = Tstripper.getText(doc);
        String[] lines = st.split("\\r?\\n");
        
        for (String line : lines) {
          text.add(line);
          obj.put("text", text);
        }
        
        doc = PDDocument.load(new File(file_name));
        PDFRenderer pdfRenderer = new PDFRenderer(doc);
        for (int page = 0; page < doc.getNumberOfPages(); page++) {
          BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300.0F, ImageType.RGB);
          String fileName = "image_" + page + ".png";
          images.add(fileName);
          obj.put("images", images);
          ImageIOUtil.writeImage(bim, fileName, 300);
          bim.flush();
        }
        
        obj.put("status", Integer.valueOf(1));
        obj.put("message", "Success");
      }
      
      hideProgressBar();
      JOptionPane.showMessageDialog(null, "Successfully generated text and images", "Success", 1);
      doc.close();
    } else {
      obj.put("status", Integer.valueOf(0));
      obj.put("message", "Failed as not file selected or file format is not valid");
      JOptionPane.showMessageDialog(null, "Image and Text generation failed", "Error", 0);
    }
    
    FileWriter file = new FileWriter("text.json", false);
    file.write(obj.toJSONString());
    file.close();
    doc.close();
    hideProgressBar();
  }
  
  private static void showProgressBar() {
    pbar = new JProgressBar();
    pbar.setIndeterminate(true);
    pbar.setVisible(true);
    frame = new JFrame("Loading...");
    frame.setBounds(600, 500, 400, 400);
    frame.setContentPane(pbar);
    frame.pack();
    frame.setVisible(true);
  }
  
  private static void hideProgressBar() {
    frame.setVisible(false);
    pbar.setVisible(false);
  }
}
