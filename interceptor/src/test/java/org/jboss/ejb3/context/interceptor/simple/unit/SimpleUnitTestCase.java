/*
 * JBoss, Home of Professional Open Source
 * Copyright (c) 2010, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ejb3.context.interceptor.simple.unit;

import javassist.util.proxy.MethodHandler;
import org.jboss.ejb3.context.interceptor.simple.InjectingInterceptionHandlerFactory;
import org.jboss.ejb3.context.interceptor.simple.SimpleBean;
import org.jboss.ejb3.context.interceptor.simple.TransformResultInterceptor;
import org.jboss.interceptor.model.InterceptionModel;
import org.jboss.interceptor.model.InterceptionModelBuilder;
import org.jboss.interceptor.model.metadata.ReflectiveClassReference;
import org.jboss.interceptor.proxy.InterceptionHandlerFactory;
import org.jboss.interceptor.proxy.InterceptorProxyCreator;
import org.jboss.interceptor.proxy.InterceptorProxyCreatorImpl;
import org.jboss.interceptor.registry.InterceptorMetadataRegistry;
import org.jboss.interceptor.registry.InterceptorRegistry;
import org.jboss.interceptor.registry.SimpleClassMetadataReader;
import org.jboss.interceptor.util.InterceptionUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class SimpleUnitTestCase
{
   private static InterceptorMetadataRegistry interceptorMetadataRegistry = new InterceptorMetadataRegistry(SimpleClassMetadataReader.getInstance());

   // for some reason removed from 1.0.0-CR11 InterceptionUtils
   private static <T> T proxifyInstance(T instance, Class<T> type, InterceptorRegistry<Class<?>, ?> interceptorRegistry, InterceptionHandlerFactory<?> interceptorHandlerFactory)
   {
      InterceptorProxyCreator ipc = new InterceptorProxyCreatorImpl(interceptorRegistry, interceptorHandlerFactory);
      MethodHandler methodHandler = ipc.createMethodHandler(instance, type, interceptorMetadataRegistry.getInterceptorClassMetadata(ReflectiveClassReference.of(type), true));
      return ipc.createProxyInstance(InterceptionUtils.createProxyClassWithHandler(type, methodHandler), methodHandler);
   }

   @Test
   public void test1()
   {
      Class<SimpleBean> cls = SimpleBean.class;
      SimpleBean instance = new SimpleBean();
      InterceptorRegistry<Class<?>,Class<?>> interceptorRegistry = new InterceptorRegistry<Class<?>, Class<?>>();
      InterceptionModelBuilder<Class<?>, Class<?>> builder = InterceptionModelBuilder.newBuilderFor(cls, (Class) Class.class);
      
      // TODO: I really want to just consume the annotations
      builder.interceptAll().with(TransformResultInterceptor.class);

      InterceptionModel<Class<?>, Class<?>> interceptionModel = builder.build();
      interceptorRegistry.registerInterceptionModel(cls, interceptionModel);

      Map<String, Object> injectionEnv = new HashMap<String, Object>();

      SimpleBean proxy = proxifyInstance(instance, cls, interceptorRegistry, new InjectingInterceptionHandlerFactory(interceptorMetadataRegistry, injectionEnv));
      
      InterceptionUtils.executePostConstruct(proxy);
      
      String result = proxy.concat("first", "second");
      System.out.println(result);
   }
}
