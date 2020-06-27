/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jr.testy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import com.jr.comment.CommentProvider;
import com.jr.comment.Comments;

/**
 *
 * @author Julian Ragan
 */
public class Controller {
    private OknoTestowe ot;
    private CommentProvider provider;
    
    public Controller(OknoTestowe ot){
        this.ot = ot;
        JTextField txtPath = (JTextField)ot.getComp("txtPath");
        txtPath.setEditable(false);
        JTextField txtAuthorId = (JTextField)ot.getComp("txtAuthorId");
        PlainDocument doc = (PlainDocument) txtAuthorId.getDocument();
        doc.setDocumentFilter(new IntFilter());
        txtAuthorId.addFocusListener(new TxtFocus());
        JTextField txtAuthorName = (JTextField)ot.getComp("txtAuthorName");
        txtAuthorName.addFocusListener(new TxtFocus());
        JButton btnPath = (JButton)ot.getComp("btnPath");
        btnPath.addActionListener(new PathActionListener());
        JButton btnLoad = (JButton)ot.getComp("btnLoad");
        btnLoad.addActionListener(new LoadActionListener());
        btnLoad.setEnabled(false);
    }
    
    private void errorDialog(String message) {
    	JOptionPane.showMessageDialog(ot, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    class PathActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showOpenDialog(ot);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                JTextField txtPath = (JTextField)ot.getComp("txtPath");
                txtPath.setText(file.getPath());
                JButton btnLoad = (JButton)ot.getComp("btnLoad");
                btnLoad.setEnabled(true);
            }
		}	   	
    }
    
    class LoadActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			JTextField txtPath = (JTextField)ot.getComp("txtPath");
			JTextField txtAuthorId = (JTextField)ot.getComp("txtAuthorId");
			JTextField txtAuthorName = (JTextField)ot.getComp("txtAuthorName");
			int id = 0;
			if(!txtAuthorId.getText().contentEquals("Id Integer")) {
				try {
					id = Integer.parseInt(txtAuthorId.getText());
				}catch(NumberFormatException e1) {
					e1.printStackTrace();
				}
			}else {
				txtAuthorId.setText("1");
				id = 1;
			}
			if(txtAuthorName.getText().contentEquals("Full Name")) {
				txtAuthorName.setText("Jan Kowalski");
			}
			String fullName = txtAuthorName.getText();
			if (!txtPath.getText().contentEquals("set path")) {
				try {
					XMLCommentDocument doc = new XMLCommentDocument(txtPath.getText());
					XMLCommentProvider xProv = new XMLCommentProvider(doc, id);
					Comments c = (Comments)ot.getComp("comments");
					c.getController().registerAuthor(id, fullName);
					c.getController().registerCommentProvider(xProv);
				} catch (DOMException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ParserConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SAXException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		}
    	
    }

    
    class TxtFocus implements FocusListener{

		@Override
		public void focusGained(FocusEvent e) {
			JTextField jt = (JTextField)e.getSource();
			jt.setSelectionStart(0);
			jt.setSelectionEnd(jt.getText().length());
		}

		@Override
		public void focusLost(FocusEvent e) {} //Empty
    }
    
    
    class IntFilter extends DocumentFilter {
    	   @Override
    	   public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
    	      Document doc = fb.getDocument();
    	      StringBuilder sb = new StringBuilder();
    	      sb.append(doc.getText(0, doc.getLength()));
    	      sb.insert(offset, string);
    	      if (test(sb.toString())) {
    	         super.insertString(fb, offset, string, attr);
    	      } else {
    	         errorDialog("Integer values only");
    	      }
    	   }

    	   @Override
    	   public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
    	      Document doc = fb.getDocument();
    	      StringBuilder sb = new StringBuilder();
    	      sb.append(doc.getText(0, doc.getLength()));
    	      sb.replace(offset, offset + length, text);
    	      if (test(sb.toString())) {
    	         super.replace(fb, offset, length, text, attrs);
    	      } else {
    	    	  errorDialog("Integer values only");
    	      }
    	   }

    	   @Override
    	   public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
    	      Document doc = fb.getDocument();
    	      StringBuilder sb = new StringBuilder();
    	      sb.append(doc.getText(0, doc.getLength()));
    	      sb.delete(offset, offset + length);
    	      if (test(sb.toString())) {
    	         super.remove(fb, offset, length);
    	      } else {
    	    	  errorDialog("Integer values only");
    	      }
    	   }
    	   
    	   private boolean test(String text) {
     	      try {
     	         Integer.parseInt(text);
     	         return true;
     	      } catch (NumberFormatException e) {
     	         return false;
     	      }
     	   }
    	}
}
