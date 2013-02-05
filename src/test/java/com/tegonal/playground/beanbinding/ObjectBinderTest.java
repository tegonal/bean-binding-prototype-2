package com.tegonal.playground.beanbinding;

import org.jdesktop.beansbinding.BeanProperty;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ObjectBinderTest {

	@Test
	public void testReadSimpleProperty() {
		ObjectBinder<TestParentBean> objectBinder = new ObjectBinder<TestParentBean>(
			TestParentBean.class);
		TestParentBean modelTemplate = objectBinder.getModelTemplate();
		modelTemplate.getType();
		BeanProperty<TestParentBean, Object> beanProperty = objectBinder.getBeanProperty();

		String testString = "testString";

		TestParentBean testParentBean = new TestParentBean();
		testParentBean.setType(testString);

		Object value = beanProperty.getValue(testParentBean);
		Assert.assertEquals(testString, value);
	}

	@Test
	public void testWriteSimpleProperty() {
		ObjectBinder<TestParentBean> objectBinder = new ObjectBinder<TestParentBean>(
			TestParentBean.class);
		TestParentBean modelTemplate = objectBinder.getModelTemplate();
		modelTemplate.getType();
		BeanProperty<TestParentBean, Object> beanProperty = objectBinder.getBeanProperty();

		String testString = "testString";

		TestParentBean testParentBean = new TestParentBean();

		beanProperty.setValue(testParentBean, testString);
		Assert.assertEquals(testString, testParentBean.getType());
	}

	@Test
	public void testReadNestedProperty() {
		ObjectBinder<TestBean> objectBinder = new ObjectBinder<TestBean>(TestBean.class);
		TestBean modelTemplate = objectBinder.getModelTemplate();
		modelTemplate.getParent().getType();
		BeanProperty<TestBean, Object> beanProperty = objectBinder.getBeanProperty();

		String testString = "testString";

		TestBean testBean = new TestBean();

		TestParentBean testParentBean = new TestParentBean();
		testParentBean.setType(testString);

		testBean.setParent(testParentBean);

		Object value = beanProperty.getValue(testBean);
		Assert.assertEquals(testString, value);
	}

	@Test
	public void testWriteNestedProperty() {
		ObjectBinder<TestBean> objectBinder = new ObjectBinder<TestBean>(TestBean.class);
		TestBean modelTemplate = objectBinder.getModelTemplate();
		modelTemplate.getParent().getType();
		BeanProperty<TestBean, Object> beanProperty = objectBinder.getBeanProperty();

		String testString = "testString";

		TestBean testBean = new TestBean();

		TestParentBean testParentBean = new TestParentBean();

		testBean.setParent(testParentBean);

		beanProperty.setValue(testBean, testString);
		Assert.assertEquals(testString, testParentBean.getType());
	}

	public static class TestBean {

		private String m_name;
		private String m_city;
		private TestParentBean m_parent;

		public String getName() {
			return m_name;
		}

		public void setName(String name) {
			m_name = name;
		}

		public String getCity() {
			return m_city;
		}

		public void setCity(String city) {
			m_city = city;
		}

		public TestParentBean getParent() {
			return m_parent;
		}

		public void setParent(TestParentBean parent) {
			m_parent = parent;
		}
	}

	public static class TestParentBean {

		private String m_type;

		public String getType() {
			return m_type;
		}

		public void setType(String type) {
			m_type = type;
		}
	}
}
