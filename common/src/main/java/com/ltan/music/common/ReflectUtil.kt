package com.ltan.music.common

import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * TMusic.com.ltan.music.common
 *
 * @ClassName: ReflectUtil
 * @Description:
 * @Author: tanlin
 * @Date:   2019-04-28
 * @Version: 1.0
 */
object ReflectUtil {

    /**
     * When `Type` initialized with a value of an object, its fully qualified class name
     * will be prefixed with this.
     *
     * @see ReflectUtil.getClassName
     */
    private const val TYPE_CLASS_NAME_PREFIX = "class "
    private const val TYPE_INTERFACE_NAME_PREFIX = "interface "

    /**
     * [Type.toString] value is the fully qualified class name prefixed
     * with [ReflectUtil.TYPE_CLASS_NAME_PREFIX]. This method will substring it, for it to be eligible
     * for [Class.forName].
     *
     * @param type the `Type` value whose class name is needed.
     * @return `String` class name of the invoked `type`.
     * @see ReflectUtil.getClass
     */
    @JvmStatic
    fun getClassName(type: Type?): String {
        if (type == null) {
            return ""
        }
        var className: String = type.toString()
        if (className.startsWith(TYPE_CLASS_NAME_PREFIX)) {
            className = className.substring(TYPE_CLASS_NAME_PREFIX.length)
        } else if (className.startsWith(TYPE_INTERFACE_NAME_PREFIX)) {
            className = className.substring(TYPE_INTERFACE_NAME_PREFIX.length)
        }
        return className
    }

    /**
     * Returns the `Class` object associated with the given [Type]
     * depending on its fully qualified name.
     *
     * @param type the `Type` whose `Class` is needed.
     * @return the `Class` object for the class with the specified name.
     * @throws ClassNotFoundException if the class cannot be located.
     * @see ReflectUtil.getClassName
     */
    @Throws(ClassNotFoundException::class)
    fun getClass(type: Type): Class<*>? {
        val className = getClassName(type)
        return if (className.isEmpty()) {
            null
        } else Class.forName(className)
    }

    /**
     * Creates a new instance of the class represented by this `Type` object.
     *
     * @param type the `Type` object whose its representing `Class` object
     * will be instantiated.
     * @return a newly allocated instance of the class represented by
     * the invoked `Type` object.
     * @throws ClassNotFoundException if the class represented by this `Type` object
     * cannot be located.
     * @throws InstantiationException if this `Type` represents an abstract class,
     * an interface, an array class, a primitive type, or void;
     * or if the class has no nullary constructor;
     * or if the instantiation fails for some other reason.
     * @throws IllegalAccessException if the class or its nullary constructor is not accessible.
     * @see Class.newInstance
     */
    @Throws(ClassNotFoundException::class, InstantiationException::class, IllegalAccessException::class)
    fun newInstance(type: Type): Any? {
        val clazz = getClass(type) ?: return null
        return clazz.newInstance()
    }

    /**
     * Returns an array of `Type` objects representing the actual type
     * arguments to this object.
     * If the returned value is null, then this object represents a non-parameterized
     * object.
     *
     * @param object the `object` whose type arguments are needed.
     * @return an array of `Type` objects representing the actual type
     * arguments to this object.
     * @see Class.getGenericSuperclass
     * @see ParameterizedType.getActualTypeArguments
     */
    fun getParameterizedTypes(`object`: Any): Array<Type>? {
        var superclassType = `object`.javaClass.getGenericSuperclass()
        if (!ParameterizedType::class.java!!.isAssignableFrom(superclassType!!.javaClass)) {
            superclassType = `object`.javaClass.getSuperclass()!!.getGenericSuperclass()
            if (!ParameterizedType::class.java!!.isAssignableFrom(superclassType!!.javaClass)) {
                return null
            }
        }

        return (superclassType as ParameterizedType).actualTypeArguments
    }

    /**
     * Checks whether a `Constructor` object with no parameter types is specified
     * by the invoked `Class` object or not.
     *
     * @param clazz the `Class` object whose constructors are checked.
     * @return `true` if a `Constructor` object with no parameter types is specified.
     * @throws SecurityException If a security manager, *s* is present and any of the
     * following conditions is met:
     *
     *  *  invocation of
     * [                           s.checkMemberAccess(this, Member.PUBLIC)][SecurityManager.checkMemberAccess] denies
     * access to the constructor
     *
     *
     *  *  the caller's class loader is not the same as or an
     * ancestor of the class loader for the current class and
     * invocation of [                           s.checkPackageAccess()][SecurityManager.checkPackageAccess] denies access to the package
     * of this class
     *
     * @see Class.getConstructor
     */
    @Throws(SecurityException::class)
    fun hasDefaultConstructor(clazz: Class<*>): Boolean {
        val empty = arrayOf<Class<*>>()
        try {
            clazz.getConstructor(*empty)
        } catch (e: NoSuchMethodException) {
            return false
        }

        return true
    }

    /**
     * Returns a `Class` object that identifies the
     * declared class for the field represented by the given `String name` parameter inside
     * the invoked `Class<?> clazz` parameter.
     *
     * @param clazz the `Class` object whose declared fields to be
     * checked for a certain field.
     * @param name  the field name as `String` to be
     * compared with [Field.getName]
     * @return the `Class` object representing the type of given field name.
     * @see Class.getDeclaredFields
     * @see Field.getType
     */
    fun getFieldClass(clazz: Class<*>?, name: String?): Class<*>? {
        if (clazz == null || name == null || name.isEmpty()) {
            return null
        }

        var propertyClass: Class<*>? = null

        for (field in clazz.declaredFields) {
            field.isAccessible = true
            if (field.name.equals(name, ignoreCase = true)) {
                propertyClass = field.type
                break
            }
        }

        return propertyClass
    }

    /**
     * Returns a `Class` object that identifies the
     * declared class as a return type for the method represented by the given
     * `String name` parameter inside the invoked `Class<?> clazz` parameter.
     *
     * @param clazz the `Class` object whose declared methods to be
     * checked for the wanted method name.
     * @param name  the method name as `String` to be
     * compared with [Method.getName]
     * @return the `Class` object representing the return type of the given method name.
     * @see Class.getDeclaredMethods
     * @see Method.getReturnType
     */
    fun getMethodReturnType(clazz: Class<*>?, name: String?): Class<*>? {
        var name = name
        if (clazz == null || name == null || name.isEmpty()) {
            return null
        }

        name = name.toLowerCase()
        var returnType: Class<*>? = null

        for (method in clazz.declaredMethods) {
            if (method.name == name) {
                returnType = method.returnType
                break
            }
        }

        return returnType
    }

    /**
     * Extracts the enum constant of the specified enum class with the
     * specified name. The name must match exactly an identifier used
     * to declare an enum constant in the given class.
     *
     * @param clazz the `Class` object of the enum type from which
     * to return a constant.
     * @param name  the name of the constant to return.
     * @return the enum constant of the specified enum type with the
     * specified name.
     * @throws IllegalArgumentException if the specified enum type has
     * no constant with the specified name, or the specified
     * class object does not represent an enum type.
     * @see Enum.valueOf
     */
    // EnumClass.valueOf(value: String): EnumClass
    // EnumClass.values(): Array<EnumClass>
    // todo Enum constants
    fun getEnumConstant(clazz: Class<*>?, name: String?): Any? {
        return null
    }
}