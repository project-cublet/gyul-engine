/**
 * Copyright © 2019 The Project-gyul Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gyul.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * GenericUtils
 *  
 * @author sungjae
 */
public class GenericUtils {
	
	@SuppressWarnings("rawtypes")
	public static Class getInterfacesGenericType(Class<?> clazz, Class<?> interfaze) {
		if(clazz.equals(Object.class)) {
			return null;
		}
		for (Type type : clazz.getGenericInterfaces()) {
			if (type instanceof ParameterizedType) {
				ParameterizedType p = (ParameterizedType) type;
				if (interfaze.isAssignableFrom((Class) p.getRawType())
						&& p.getActualTypeArguments().length > 0) {
					return (Class) p.getActualTypeArguments()[0];
				}
			}
		}
		//부모 클래스를 재귀적으로 탐색한다
		return getInterfacesGenericType(clazz.getSuperclass(), interfaze);
	}
}
