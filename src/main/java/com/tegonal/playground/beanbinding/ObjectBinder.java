package com.tegonal.playground.beanbinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.jdesktop.beansbinding.BeanProperty;
import org.mockito.internal.creation.MockSettingsImpl;
import org.mockito.internal.progress.MockingProgress;
import org.mockito.internal.progress.ThreadSafeMockingProgress;
import org.mockito.internal.stubbing.defaultanswers.ReturnsDeepStubs;
import org.mockito.internal.util.MockUtil;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.OngoingStubbing;

public class ObjectBinder<O> {

	private static final String[] PREFIXES = new String[] {"get", "set", "is", "has"};

	private final MockUtil mockUtil = new MockUtil();
	private final MockingProgress mockingProgress = new ThreadSafeMockingProgress();

	private MockingCache m_mockingCache = new MockingCache();

	private final Class<O> m_clazz;

	public ObjectBinder(Class<O> clazz) {
		m_clazz = clazz;
	}

	/**
	 * @return type template used to declare gui bindings
	 */
	public O getModelTemplate() {
		MockSettingsImpl settings = (MockSettingsImpl)new MockSettingsImpl()
			.defaultAnswer(new EXReturnsDeepStubs(m_mockingCache));
		O mock = mockUtil.createMock(m_clazz, settings);
		mockingProgress.mockingStarted(mock, m_clazz, settings);
		return mock;

		// return Mockito.mock(getGUIModelType(),
		// Mockito.withSettings().defaultAnswer(Mockito.RETURNS_DEEP_STUBS));
	}

	public Stack<InvocationOnMock> getLatestInvocationStack() {
		mockingProgress.stubbingStarted();
		@SuppressWarnings("rawtypes")
		OngoingStubbing stubbing = (OngoingStubbing)mockingProgress.pullOngoingStubbing();
		Object mock = stubbing.getMock();
		stubbing.thenCallRealMethod();
		return m_mockingCache.retrieveMockInvocationStack(mock);
	}

	private static class EXReturnsDeepStubs extends ReturnsDeepStubs {

		private static final long serialVersionUID = 1880837724076720541L;

		private final MockingCache m_cache;
		private Map<Object, Object> m_follower = new HashMap<Object, Object>();

		public EXReturnsDeepStubs(MockingCache cache) {
			m_cache = cache;
		}

		@Override
		public Object answer(InvocationOnMock invocation) throws Throwable {
			// Class<?> clz = invocation.getMethod().getReturnType();
			Object answer = super.answer(invocation);
			// if (new MockCreationValidator().isTypeMockable(clz)) {
			// return will be another mock
			Object mock = invocation.getMock();
			Object parent = m_follower.get(mock);
			if (parent != null) {
				m_cache.registerInvocation(parent, mock, invocation);
			}
			else {
				m_cache.registerInvocation(mock, invocation);
			}

			// register the following mock
			m_follower.put(answer, mock);
			// }

			return answer;
		}
	}

	public String getBeanPropertyPath() {
		// convert stacktrace into bean property
		Stack<InvocationOnMock> latestInvocationStack = getLatestInvocationStack();
		StringBuffer buf = new StringBuffer();
		for (InvocationOnMock invocation : latestInvocationStack) {
			String methodName = invocation.getMethod().getName();

			for (String prefix : PREFIXES) {
				if (methodName.startsWith(prefix)) {
					int len = prefix.length();
					methodName = String.valueOf(methodName.charAt(len)).toLowerCase()
							+ methodName.substring(len + 1);
					break;
				}
			}

			if (buf.length() > 0) {
				buf.append('.');
			}
			buf.append(methodName);
		}
		return buf.toString();
	}

	public <S> BeanProperty<O, S> getBeanProperty() {
		return BeanProperty.create(getBeanPropertyPath());
	}

	public void dispose() {
		m_mockingCache.clear();
	}

	public static class MockingCache {

		public HashMap<Object, Stack<InvocationOnMock>> m_invocationCache = new HashMap<Object, Stack<InvocationOnMock>>();

		public void registerInvocation(Object mock, InvocationOnMock invocation) {
			Stack<InvocationOnMock> stack = m_invocationCache.get(mock);
			if (stack == null) {
				stack = new Stack<InvocationOnMock>();
				m_invocationCache.put(mock, stack);
			}
			stack.push(invocation);
		}

		public void registerInvocation(Object parent, Object mock, InvocationOnMock invocation) {
			Stack<InvocationOnMock> stack = m_invocationCache.get(parent);
			if (stack == null) {
				stack = new Stack<InvocationOnMock>();
			}
			stack.push(invocation);
			m_invocationCache.put(mock, stack);
			m_invocationCache.remove(parent);
		}

		public void clear() {
			m_invocationCache.clear();
		}

		public Stack<InvocationOnMock> retrieveMockInvocationStack(Object mock) {
			// m_invocationCache.get(mock);
			return m_invocationCache.remove(mock);
		}
	}
}
