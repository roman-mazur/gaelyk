package groovyx.gaelyk.plugins

import java.util.Map;

import groovy.lang.GroovyObjectSupport;
import groovy.lang.MissingPropertyException;

/**
 * A plugin script delegate that provides access to variables from the servlet binding.
 * @author Roman Mazur
 */
class PluginDelegate extends GroovyObjectSupport {

  /** Binding for the local thread. */
  private static ThreadLocal<Binding> localThreadBinding = new ThreadLocal<Binding>()
  
  /** Contributed binding variables. */
  Map bindingVariables = [:]
  
  /**
   * Define the current binding for plugins.
   * @param b binding extension for plugins
   */
  static void defineCurrentBinding(final Binding b) {
    localThreadBinding.set b
  }
  
  @Override
  void setProperty(final String name, final Object value) {
    bindingVariables[name] = value // contribute new variable
  }
  
  @Override
  def getProperty(final String name) {
    // try what plugin has defined itself
    def res = bindingVariables[name]
    if (res != null) { return res }

    // resolution
    Binding b = localThreadBinding.get()
    if (b == null) {
      throw new MissingPropertyException(name, this.getClass());
    }
    return b.getVariable(name)
  }
  
  @Override
  public String toString() {
    return "PluginDelegate[contributed=${bindingVariables}]"
  }
  
}
