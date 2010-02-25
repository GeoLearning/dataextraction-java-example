package dataextract;

import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.*;

public class CsvTest {
    @Test
    public void testThatInputStreamIsParsedToAMap() {
        Csv csv = new Csv(new ByteArrayInputStream("col1,col2 \n row1data1,row1data2 \n row2data1,row2data2 \n".getBytes()));
        assertEquals(2, csv.size());
        assertEquals("row1data1", csv.row(0).get("col1"));
        assertEquals("row1data2", csv.row(0).get("col2"));
        assertEquals("row2data1", csv.row(1).get("col1"));
        assertEquals("row2data2", csv.row(1).get("col2"));
    }
}
