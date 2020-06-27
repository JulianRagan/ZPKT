/**
 * 
 */
package com.jr.testy;

import com.jr.comment.CommentProvider;
import com.jr.data.CommentContainer;
import com.jr.data.IntStrPair;
import java.util.Date;
import java.util.List;

/**
 * @author Julian Ragan
 *
 */
public class XMLCommentProvider implements CommentProvider {
	private CommentContainer cCC;
	private XMLCommentDocument doc;
	private int authorId;
	private List<IntStrPair> cTags;
	private int tagPointer = -1;

	
	public XMLCommentProvider(XMLCommentDocument doc, int authorId) {
		this.doc = doc;
		this.authorId = authorId;
	}
	
    @Override
    public boolean next() {
        if (doc.next()) {
        	cCC = doc.getComment();
        	cTags = cCC.getTags();
        	tagPointer = -1;
        	return true;
        }else {
        	return false;
        }
    }

    /**
     * For the sake of testing, edit permission is granted if provided author id equals stored author id 
     */
    @Override
    public boolean editPermission() {
        if (cCC.getAuthorId() == authorId) {
        	return true;
        }else {
        	return false;
        }
    }

    /**
     * For the sake of testing, delete permission is granted if provided author id equals stored author id 
     */
    @Override
    public boolean deletePermission() {
        if (cCC.getAuthorId() == authorId) {
        	return true;
        }else {
        	return false;
        }
    }

    /**
     * For the sake of testing, add permission is always granted 
     */
    @Override
    public boolean addPermission() {
        return true;
    }

    @Override
    public int addComment(CommentContainer cc) {
        return doc.addComment(cc);
    }

    @Override
    public void updateComment(CommentContainer cc) {
        doc.updateComment(cc);
    }

    @Override
    public void removeComment(CommentContainer cc) {
        //Do nothing
    }

    @Override
    public String getPlainTextContent() {
        return cCC.getPlainTextContent();
    }

    @Override
    public String getAuthor() {
        return cCC.getAuthor();
    }

    @Override
    public String getHtmlContent() {
        return cCC.getHtmlContent();
    }

    @Override
    public String getLastEditAuthor() {
        return cCC.getLastEditAuthor();
    }

    @Override
    public Date getTimestamp() {
        return cCC.getTimestamp();
    }

    @Override
    public Date getLastEditTimestamp() {
        return cCC.getLastEditTimestamp();
    }

    @Override
    public int getCommentId() {
        return cCC.getCommentId();
    }

    @Override
    public int getReferencedId() {
        return cCC.getReferencedId();
    }

    @Override
    public int getAuthorId() {
        return cCC.getAuthorId();
    }

    @Override
    public int getLastEditAuthorId() {
        return cCC.getLastEditAuthorId();
    }

    @Override
    public int getRefStart() {
        return cCC.getRefStart();
    }

    @Override
    public int getRefStop() {
        return cCC.getRefStop();
    }

    @Override
    public boolean nextTag() {
    	if (tagPointer + 1 < cTags.size()) {
			tagPointer++;
			return true;
		}else {
			return false;
		}
    }

    @Override
    public String getTagValue() {
        return cTags.get(tagPointer).getValue();
    }

    @Override
    public int getTagId() {
        return cTags.get(tagPointer).getKey();
    }
    
}
