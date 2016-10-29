/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.7
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package csnd6;

public class IntToStringMap {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected IntToStringMap(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(IntToStringMap obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        csndJNI.delete_IntToStringMap(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public IntToStringMap() {
    this(csndJNI.new_IntToStringMap__SWIG_0(), true);
  }

  public IntToStringMap(IntToStringMap arg0) {
    this(csndJNI.new_IntToStringMap__SWIG_1(IntToStringMap.getCPtr(arg0), arg0), true);
  }

  public long size() {
    return csndJNI.IntToStringMap_size(swigCPtr, this);
  }

  public boolean empty() {
    return csndJNI.IntToStringMap_empty(swigCPtr, this);
  }

  public void clear() {
    csndJNI.IntToStringMap_clear(swigCPtr, this);
  }

  public String get(int key) {
    return csndJNI.IntToStringMap_get(swigCPtr, this, key);
  }

  public void set(int key, String x) {
    csndJNI.IntToStringMap_set(swigCPtr, this, key, x);
  }

  public void del(int key) {
    csndJNI.IntToStringMap_del(swigCPtr, this, key);
  }

  public boolean has_key(int key) {
    return csndJNI.IntToStringMap_has_key(swigCPtr, this, key);
  }

}