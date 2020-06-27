package com.jr.testy;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.CharacterData;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import com.jr.data.CommentContainer;
import com.jr.data.IntStrPair;

public class XMLCommentDocument {
	private List<CommentContainer> comments;
	private List<IntStrPair> availableTags;
	private Document doc;
	private String filePath;
	private Element rootElement;
	private SimpleDateFormat sf;
	private int maxCommentId = 0;
	private int maxTagId = 0;
	private int commentPointer = -1;
	
	public XMLCommentDocument(String filePath) throws ParserConfigurationException, SAXException, IOException, DOMException, ParseException {
		sf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		this.filePath = filePath;
		File file = new File(filePath);
		if(file.exists()) {
			loadComments();
		}else {
			
			emptyComments();
		}
	}
	
	private void loadComments() throws ParserConfigurationException, SAXException, IOException, DOMException, ParseException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		doc = dBuilder.parse(new File(filePath));
		doc.getDocumentElement().normalize();
		NodeList nodesList = doc.getElementsByTagName("comment");
		comments = new ArrayList<CommentContainer>();
		availableTags = new ArrayList<IntStrPair>();
		for(int i = 0; i < nodesList.getLength(); i++ ) {
			Node node = nodesList.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				CommentContainer cc = new CommentContainer();
				Element el = (Element)node;
				int id = Integer.parseInt(el.getAttribute("cid"));
				cc.setCommentId(id);
				if (id > maxCommentId) {
					maxCommentId = id;
				}
				Element author = (Element)el.getElementsByTagName("author").item(0);
				cc.setAuthor(author.getTextContent());
				cc.setAuthorId(Integer.parseInt(author.getAttribute("aid")));
				cc.setTimestamp(sf.parse(el.getElementsByTagName("timestamp").item(0).getTextContent()));
				cc.setPlainTextContent(el.getElementsByTagName("contents").item(0).getTextContent());
				Node html = el.getElementsByTagName("HTMLContent").item(0);
				NodeList htmlChildren = html.getChildNodes();
				for(int j = 0; j < htmlChildren.getLength(); j++) {
					if(htmlChildren.item(j) instanceof CharacterData) {
						CharacterData cd = (CharacterData)htmlChildren.item(j);
						cc.setHtmlContent(cd.getData());
						break;
					}
				}
				Element co = (Element)el.getElementsByTagName("commentingon").item(0);
				cc.setReferencedId(Integer.parseInt(co.getAttribute("coid")));
				cc.setRefStart(Integer.parseInt(co.getAttribute("cstart")));
				cc.setRefStop(Integer.parseInt(co.getAttribute("cstop")));
				Element editor = (Element)el.getElementsByTagName("editor").item(0);
				cc.setLastEditAuthorId(Integer.parseInt(editor.getAttribute("eid")));
				cc.setLastEditAuthor(editor.getTextContent());
				cc.setLastEditTimestamp(sf.parse(el.getElementsByTagName("edittimestamp").item(0).getTextContent()));
				NodeList tagList = el.getElementsByTagName("tag"); 
				for (int j = 0; j < tagList.getLength(); j++) {
					Node tagNode = tagList.item(j);
					if(tagNode.getNodeType() == Node.ELEMENT_NODE) {
						Element tag = (Element)tagNode;
						int tagId = Integer.parseInt(tag.getAttribute("tid"));
						if (tagId > maxTagId) {
							maxTagId = tagId;
						}
						IntStrPair newTag = new IntStrPair(tagId, tag.getTextContent());
						addTagToList(newTag);
						cc.addTag(newTag);
					}
				}
				comments.add(cc);
			}
		}
	}
	
	private void emptyComments() throws ParserConfigurationException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		doc = dBuilder.newDocument();
		rootElement = doc.createElement("comments");
		comments = new ArrayList<CommentContainer>();
		availableTags = new ArrayList<IntStrPair>();
	}
	
	private void addTagToList(IntStrPair tag) {
		for(IntStrPair tag1:availableTags) {
			if(tag1.equals(tag)) {
				return; // no duplicatates
			}
		}
		availableTags.add(tag);
	}
	
	public void changePath(String filePath){
		this.filePath = filePath;
	}
	
	public boolean next() {
		if (commentPointer + 1 < comments.size()) {
			commentPointer++;
			return true;
		}else {
			return false;
		}
	}
	
	public CommentContainer getComment() throws NoSuchElementException{
		if (commentPointer >= 0 && commentPointer < comments.size()) {
			return comments.get(commentPointer);
		}
		throw new NoSuchElementException();
	}
	
	public int addComment(CommentContainer cc) {
		maxCommentId++;
		cc.setCommentId(maxCommentId);
		comments.add(cc);
		return maxCommentId;
	}
	
	public void updateComment(CommentContainer cc) {
		for(CommentContainer cc1:comments) {
			if(cc1.getCommentId() == cc.getCommentId()) {
				cc1.updateContent(cc1);
				break;
			}
		}
	}
	
	public void removeComment(CommentContainer cc) {
		for(int i = 0; i < comments.size(); i++) {
			if(comments.get(i).getCommentId() == cc.getCommentId()) {
				comments.remove(i);
				break;
			}
		}
	}
}
