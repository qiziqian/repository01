package cn.itheima.practice;

import com.hankcs.lucene.HanLPAnalyzer;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class CreateIndex2 {
    @Test
    public void TestCreateIndex() throws IOException {
        String url = "f:\\index47";
        FSDirectory directory = FSDirectory.open(Paths.get(url));
        Analyzer analyzer = new HanLPAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter writer = new IndexWriter(directory, config);

        File file = new File("f:\\searchsource");
        File[] files = file.listFiles();
        for (File file1 : files) {
            String fileName = file1.getName();
            String fileContent = FileUtils.readFileToString(file1);
            String filePath = file.getPath();
            Long fileSize = FileUtils.sizeOf(file1);

            Field fileNameField = new TextField("fileName", fileName, Field.Store.YES);
            Field fileContentField = new TextField("fileContent", fileContent, Field.Store.YES);
            Field filePathField = new StoredField("filePath", filePath);
            Field fileSizeField = new StoredField("fileSize", fileSize);

            Document document = new Document();
            document.add(fileNameField);
            document.add(fileContentField);
            document.add(filePathField);
            document.add(fileSizeField);

            writer.addDocument(document);
            writer.commit();

        }
        writer.close();
    }

    @Test
    public void testQueryIndex() throws IOException {
        String url = "f:\\index45";
        FSDirectory directory = FSDirectory.open(Paths.get(url));
        IndexReader reader = DirectoryReader.open(directory);
        Analyzer analyzer = new HanLPAnalyzer();
        IndexSearcher searcher = new IndexSearcher(reader);
        Term term = new Term("fileName", "springmvc.txt");
        Query query = new TermQuery(term);
        TopDocs docs = searcher.search(query, 10);
        System.out.println("满足条件的条目数:" + docs.totalHits);
        ScoreDoc[] scoreDocs = docs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("文件Id:"+scoreDoc.doc);
            System.out.println("文件分数:"+scoreDoc.score);
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println("文件名"+doc.get("fileName"));
            System.out.println("文件路径"+doc.get("filePath"));
            System.out.println("文件大小"+doc.get("fileSize"));
            System.out.println("文件内容"+doc.get("fileContent"));
        }


    }
}
