
/**
 * Preflight is the request which is executed to download the uploaded file. This controller is made
 * specifically for content disposition based response. we could have created the similar endpoint
 * in {@code UnrestrictedFileUpload} but as framework appends the "Vulnerability name" hence created
 * a new endpoint.
 *
 * @author KSASAN preetkaran20@gmail.com
 */
@RestController
public class PreflightController {
    private UnrestrictedFileUpload unrestrictedFileUpload;

    public PreflightController(UnrestrictedFileUpload unrestrictedFileUpload) {
        this.unrestrictedFileUpload = unrestrictedFileUpload;
    }

    @RequestMapping(
            CONTENT_DISPOSITION_STATIC_FILE_LOCATION + FrameworkConstants.SLASH + "{fileName}")
    public ResponseEntity<byte[]> fetchFile(@PathVariable("fileName") String fileName)
            throws IOException {
        InputStream inputStream =
                // ruleid: tainted-file-path
                new FileInputStream(
                        unrestrictedFileUpload.getContentDispositionRoot().toFile()
                                + FrameworkConstants.SLASH
                                + fileName);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment");
        return new ResponseEntity<byte[]>(
                IOUtils.toByteArray(inputStream), httpHeaders, HttpStatus.OK);
    }

    public static void bad(@RequestParam String user)
    {
        Socket sock;
        BufferedReader filenameReader = new BufferedReader(
                new InputStreamReader(sock.getInputStream(), "UTF-8"));
        String filename = filenameReader.readLine();
        // ruleid: tainted-file-path
        BufferedReader fileReader = new BufferedReader(new FileReader("/home/" + user + "/" + filename));
        String fileLine = fileReader.readLine();
        while(fileLine != null) {
                sock.getOutputStream().write(fileLine.getBytes());
                fileLine = fileReader.readLine();
        }
    }

    public static void bad2(@RequestParam String filename)
    {
    	ApplicationContext appContext = 
    	   new ClassPathXmlApplicationContext(new String[] {"If-you-have-any.xml"});

        // ruleid: tainted-file-path
    	Resource resource = appContext.getResource("classpath:com/" + filename);

        try {
           InputStream is = resource.getInputStream();
           BufferedReader br = new BufferedReader(new InputStreamReader(is));

           String line;
           while ((line = br.readLine()) != null) {
              System.out.println(line);
           } 
           br.close();

        } catch(IOException e){
           e.printStackTrace();
        }
    }

    public static void ok(@RequestParam String filename)
    {
    	ApplicationContext appContext = 
    	   new ClassPathXmlApplicationContext(new String[] {"If-you-have-any.xml"});

        // ok: tainted-file-path
    	Resource resource = 
           appContext.getResource("classpath:com/" + org.apache.commons.io.FilenameUtils.getName(filename));

        try {
           InputStream is = resource.getInputStream();
           BufferedReader br = new BufferedReader(new InputStreamReader(is));

           String line;
           while ((line = br.readLine()) != null) {
              System.out.println(line);
           } 
           br.close();

        } catch(IOException e){
           e.printStackTrace();
        }
    }

    @Test
    public void whenResourceAsFile_thenReadSuccessful(@RequestParam String filename) throws IOException {
        // ruleid: tainted-file-path
        File resource = new ClassPathResource("data/employees.dat" + filename).getFile();
        String employees = new String(Files.readAllBytes(resource.toPath()));
        assertEquals("Joe Employee,Jan Employee,James T. Employee", employees);
    }
}
