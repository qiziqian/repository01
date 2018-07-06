package cn.itheima.practice;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class CreateIdnex {

   @Test
    public void testCreateIndex() throws IOException {
       String url = "f:\\index44";
       FSDirectory directory = FSDirectory.open(Paths.get(url));
       Analyzer analyzer = new StandardAnalyzer();
       IndexWriterConfig config = new IndexWriterConfig(analyzer);

       IndexWriter writer = new IndexWriter(directory,config);

       File file = new File("f:\\searchsource");

       File[] files = file.listFiles();
       for (File file1 :files){
          //获取文件的四个属性
           String fileName = file1.getName();
           String fileContent = FileUtils.readFileToString(file1);
           String filePath = file1.getPath();
           Long fileSize = FileUtils.sizeOf(file1);

            //存入域中
           Field fileNameField = new TextField("fileName",fileName, Field.Store.YES);
           Field fileContextField = new TextField("fileContent",fileContent,Field.Store.YES);

           Field filePathField = new StoredField("filePath",filePath);
         //  Field fileSizeField = new StoredField("fileSize",fileSize);
           Field fileSizeField= new LongPoint("fileSize",fileSize);

          Document document = new Document();
          document.add(fileContextField);
          document.add(fileNameField);
          document.add(filePathField);
          document.add(fileSizeField);

          //写入索引库
          writer.addDocument(document);
          writer.commit();
       }
       writer.close();


   }
   @Test
    public void testQueryIndex() throws IOException {
       String url = "f:\\index44";
       FSDirectory directory =FSDirectory.open(Paths.get(url));
       IndexReader reader = DirectoryReader.open(directory);

       IndexSearcher searcher = new IndexSearcher(reader);

       Term term = new Term("fileContent","learn");

       Query query = new TermQuery(term);

       TopDocs docs = searcher.search(query,10);

       System.out.println("满足条件的条目数:"+docs.totalHits);

       ScoreDoc[] scoreDocs = docs.scoreDocs;

       for(ScoreDoc scoreDoc:scoreDocs){
           System.out.println("文档Id:"+scoreDoc.doc);
           System.out.println("文档分数:"+scoreDoc.score);
            //获取该文档
           Document document = searcher.doc(scoreDoc.doc);

           System.out.println("文档名称:"+document.get("fileName"));
           System.out.println("文档大小:"+document.get("fileSize"));
           System.out.println("文档路径:"+document.get("filePath"));
           System.out.println("文档内容:"+document.get("fileContent"));
       }




   }

}
