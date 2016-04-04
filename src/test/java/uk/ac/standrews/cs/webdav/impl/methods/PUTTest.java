package uk.ac.standrews.cs.webdav.impl.methods;

import org.junit.Test;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PUTTest extends AbstractMethodTest {

    // FIXME - test fails due to some socket connection error on the localhost
    @Test
    public void putSmallFileChunkedTestSTDHttp() throws IOException {

        String uri = "/test/mediumFile.txt";
        System.out.println("Should be able to lock a non-extant URI with extant parent - result should be 201 CREATED.\n");

        String request = makeTestString(TEST_PORT, uri);
        System.out.println("---------------");
        // null host should work for loopback! not sure what the prolbem is :(
        String response = processRequest(request, null, TEST_PORT);

        showResponse(response, TEST_PORT, TEST_LABEL, "PUT " + uri);
    }

    public String makeTestString(int port, String uri) {

        String contentMedium = "\n" +
                "\n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum dictum libero ac pellentesque. Nulla venenatis, ligula dictum pellentesque dapibus, nunc lacus iaculis turpis, non sagittis nulla lectus non nunc. Donec dapibus elementum ornare. Etiam bibendum metus ut risus bibendum, sit amet mollis sem tempus. Fusce id egestas lacus, porttitor pulvinar neque. Nam mattis ex id urna accumsan interdum. Phasellus in efficitur neque, sed blandit orci. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Mauris aliquet sed augue vitae luctus. Morbi consectetur pulvinar dui eleifend iaculis. Nam non elit neque.\n" +
                "\n" +
                "Fusce bibendum tellus non odio rhoncus, at tristique enim iaculis. Donec scelerisque vestibulum diam a tempus. In ullamcorper enim vitae ligula dignissim, quis fringilla felis mollis. Sed fringilla nunc quis nisl aliquet posuere. Etiam at quam tellus. Donec velit turpis, accumsan eu magna eget, semper rutrum metus. Sed facilisis dapibus ante. Maecenas id leo ut ligula hendrerit maximus. Vivamus eget eros ligula. Ut placerat sapien orci, et fermentum lectus ullamcorper at. Suspendisse commodo sem et euismod venenatis. Nullam pharetra ultrices arcu non pellentesque. Etiam congue nulla nec massa malesuada tincidunt. Quisque sollicitudin mi quis sem suscipit, ac iaculis quam tincidunt. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis posuere nisl non pellentesque commodo.\n" +
                "\n" +
                "Sed ac placerat diam. Pellentesque efficitur, ex nec ullamcorper rhoncus, ligula lacus dapibus arcu, at sagittis quam enim in dui. Integer non augue arcu. Curabitur dictum ultrices elit ullamcorper aliquet. Pellentesque vitae est porta tortor elementum posuere. Sed porta velit orci, eget elementum metus aliquet nec. Vivamus in facilisis ante. Sed ultrices dolor sed metus consequat, nec iaculis nibh imperdiet.\n" +
                "\n" +
                "Pellentesque pulvinar porta nisl, nec pulvinar nisl facilisis quis. Donec consequat odio interdum nulla sagittis molestie. Sed sed ornare sapien, quis euismod augue. Duis aliquam at tortor eu tristique. Aenean mattis elit vel leo eleifend, non sodales risus ornare. Sed malesuada id metus nec sagittis. Nunc libero sapien, auctor vitae porta cursus, tincidunt sed felis. Curabitur ligula sapien, accumsan in maximus vitae, efficitur imperdiet sapien.\n" +
                "\n" +
                "Aliquam gravida aliquet gravida. Duis posuere, leo quis blandit aliquet, elit augue tempus metus, eget tincidunt libero est at ligula. Proin eget sodales ligula. Nulla mollis tincidunt turpis, a luctus tortor facilisis sit amet. Nunc eu feugiat quam. Sed eget enim ipsum. Mauris mollis dignissim pharetra.\n" +
                "\n" +
                "Duis mattis nisl sit amet ipsum facilisis rutrum. Nullam sit amet purus cursus, molestie velit nec, aliquam purus. Fusce sed ex eget sem bibendum tristique eu a diam. Nulla vel orci magna. Curabitur vel fringilla lectus. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Quisque vitae est quis ex bibendum rutrum. Donec porttitor, metus gravida varius suscipit, nisi tellus rhoncus lorem, in ullamcorper elit lorem amet. ";


        return "PUT " + uri + " HTTP/1.1" + CRLF +
                "Host: " + TEST_HOST + ":" + port + CRLF +
                "User-Agent: ASA Test Harness" + CRLF +
                "Content-Length: " + contentMedium.length() + CRLF +
                "Accept: */*" + CRLF +
                "Connection: close" + CRLF + CRLF +
                contentMedium;
    }
}