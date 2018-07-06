package cn.itheima.practice;

import com.hankcs.lucene.HanLPAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

public class TestQueryIndex {
    @Test
    public void testDelete() throws IOException {
        String url = "f:\\index44";
        FSDirectory directory = FSDirectory.open(Paths.get(url));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(directory,config);
        writer.deleteAll();
       // writer.deleteDocuments(new Term("fileName","springmvc.txt"));
        writer.close();
    }

    @Test
    public  void testMatchAllDocsQuery () throws  IOException{
        Query query = new MatchAllDocsQuery();
        //执行查询
        executeQuery(query);
    }
    @Test
    public void testRangeNumberQuery() throws IOException {
        Query query = LongPoint.newRangeQuery("fileSize",0,50000);
        executeQuery(query);

    }

    @Test
    public void testTerm() throws IOException {
        Term term = new Term("fileContent","learn");
        Query query = new TermQuery(term);
        executeQuery(query);
    }
    @Test
    public void testBooleanClause() throws IOException {
        Query query1 = new TermQuery(new Term("fileContent","learn"));
        Query query2 = new TermQuery(new Term("fileName","cxf_README.txt"));
        BooleanClause bc1 = new BooleanClause(query1, BooleanClause.Occur.MUST);
        BooleanClause bc2 = new BooleanClause(query2, BooleanClause.Occur.MUST_NOT);
        BooleanQuery bq = new BooleanQuery.Builder().add(bc1).add(bc2).build();
        executeQuery(bq);
    }
    @Test
    public  void testQueryParser() throws ParseException, IOException {
        QueryParser queryParser = new QueryParser("fileContent",new HanLPAnalyzer());
        Query query = queryParser.parse("全文检索概念");
        executeQuery(query);
    }
    @Test
    public void testMultiFieldQueryParse() throws ParseException, IOException {
        MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"fileContent","fileName"},new HanLPAnalyzer());
        Query query = parser.parse("apache 全文检索概念");
        executeQuery(query);


    }














    private void executeQuery(Query query) throws IOException {
        //指定索引库位置
        String url="F:\\index44";
        //索引库目录
        FSDirectory directory = FSDirectory.open(Paths.get(url));

        //创建indexReader读对象
        DirectoryReader reader = DirectoryReader.open(directory);

        //1.创建核心查询对象
        IndexSearcher indexSearcher = new IndexSearcher(reader);

        //3.执行查询  参数一：查询对象  参数二：显示查询结果记录数据
        //TopDocs包含了：1、满足查询条件总记录数  2、文档id  3、文档得分
        TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println("满足查询条件总记录数："+topDocs.totalHits);
        //4遍历显示数据  ScoreDoc中封装了2、文档id  3、文档得分
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docId = scoreDoc.doc;
            System.out.println("文档id："+docId);
            System.out.println("文档得分："+scoreDoc.score);

            //获取文档域字段内容
            Document doc = indexSearcher.doc(docId);
            System.out.println("文档名称:"+doc.get("fileName"));
            System.out.println("文档大小:"+doc.get("fileSize"));
            System.out.println("文档路径:"+doc.get("filePath"));
            System.out.println("文档内容:"+doc.get("fileContent"));

        }
    }
}
