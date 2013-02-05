package com.tegonal.playground.beanbinding;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;


public class BeanBindingPrototype2 {

	String[] prefixes = new String[] {
			"get",
			"set",
			"is",
			"has"
};
	
	public BeanBindingPrototype2() {

		ObjectBinder<DummyPojo> binder = new ObjectBinder<BeanBindingPrototype2.DummyPojo>(
			DummyPojo.class);

		binder.getModelTemplate().getName();
		String path = binder.getBeanPropertyPath();
		BeanProperty<DummyPojo, String> name = BeanProperty.create(path);

		binder.getModelTemplate().getParent().getStreet();
		path = binder.getBeanPropertyPath();
		BeanProperty<DummyPojo, String> street = BeanProperty.create(path);

		JFrame frame = new JFrame();

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 2));

		JTextField field = new JTextField(20);
		BeanProperty<JTextField, String> textProperty = BeanProperty.create("text");
		panel.add(new JLabel("Name:"));
		panel.add(field);

		JTextField field2 = new JTextField(20);
		panel.add(new JLabel("Street:"));
		panel.add(field2);

		frame.getContentPane().add(panel);

		final DummyPojo realObject = new DummyPojo();
		final ParentPojo parent = new ParentPojo();
		realObject.setParent(parent);

		AutoBinding<DummyPojo, String, JTextField, String> binding = Bindings.createAutoBinding(
			UpdateStrategy.READ_WRITE, realObject, name, field, textProperty);
		binding.bind();

		AutoBinding<DummyPojo, String, JTextField, String> binding2 = Bindings.createAutoBinding(
			UpdateStrategy.READ_WRITE, realObject, street, field2, textProperty);
		binding2.bind();

		frame.pack();
		frame.setPreferredSize(new Dimension(200, 200));
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("Name:" + realObject.getName());
				System.out.println("Street:" + realObject.getParent().getStreet());
			}
		});
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		new BeanBindingPrototype2();
	}

	public class DummyPojo {

		private String m_name;
		private ParentPojo m_parent;

		public String getName() {
			return m_name;
		}

		public void setName(String name) {
			m_name = name;
		}

		public ParentPojo getParent() {
			return m_parent;
		}

		public void setParent(ParentPojo parent) {
			m_parent = parent;
		}
	}

	public static class ParentPojo {

		private String m_street;

		public String getStreet() {
			return m_street;
		}

		public void setStreet(String street) {
			m_street = street;
		}
	}
}
