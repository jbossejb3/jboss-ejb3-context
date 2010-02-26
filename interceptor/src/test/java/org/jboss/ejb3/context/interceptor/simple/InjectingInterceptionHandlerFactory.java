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
package org.jboss.ejb3.context.interceptor.simple;

import org.jboss.interceptor.model.InterceptorMetadata;
import org.jboss.interceptor.model.metadata.ReflectiveClassReference;
import org.jboss.interceptor.proxy.DirectClassInterceptionHandler;
import org.jboss.interceptor.proxy.InterceptionHandler;
import org.jboss.interceptor.proxy.InterceptionHandlerFactory;
import org.jboss.interceptor.registry.InterceptorMetadataRegistry;

import java.util.Map;

/**
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class InjectingInterceptionHandlerFactory implements InterceptionHandlerFactory<Class<?>>
{
   private InterceptorMetadataRegistry interceptorMetadataRegistry;
   private Map<String, Object> injectionEnv;

   public InjectingInterceptionHandlerFactory(InterceptorMetadataRegistry interceptorMetadataRegistry, Map<String, Object> injectionEnv)
   {
      this.interceptorMetadataRegistry = interceptorMetadataRegistry;
      this.injectionEnv = injectionEnv;
   }

   public InterceptionHandler createFor(Class<?> clazz)
   {
      return new InjectingInterceptionHandler(clazz, interceptorMetadataRegistry.getInterceptorClassMetadata(ReflectiveClassReference.of(clazz)));
   }

   protected class InjectingInterceptionHandler extends DirectClassInterceptionHandler<Class<?>>
   {
      public InjectingInterceptionHandler(Class<?> clazz, InterceptorMetadata interceptorMetadata)
      {
         super(clazz, interceptorMetadata);

         Object instance = getInterceptorInstance();
         // TODO: really inject
         if(instance instanceof TransformResultInterceptor)
         {
            //((TransformResultInterceptor) instance).setDomainName((String) injectionEnv.get(TransformResultInterceptor.class.getName() + ".domainName"));
         }
      }
   }
}
