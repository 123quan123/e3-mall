import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import java.io.IOException;

/**
 * @author quan
 * @create 2020-05-09 11:12
 */
public class TestSolrJ {

    @Test
    public void addDocument() throws IOException, SolrServerException {
        SolrServer solrServer = new HttpSolrServer("http://192.168.41.128:8080/solr/collection1")   ;

        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", "doc01");
        document.addField("item_title", "测试商品01");
        document.addField("item_price", 1000);

        solrServer.add(document);
        solrServer.commit();
    }
}
