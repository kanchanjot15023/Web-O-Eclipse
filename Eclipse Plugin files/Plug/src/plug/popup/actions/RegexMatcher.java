package plug.popup.actions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatcher
{
  static String stacktrace_regex = "(^\\d+\\) .+)|(^.+Exception(:)?(.+)*)|(^\\s*at .+)|(^\\s+... \\d+ more)|(^\\s*Caused by:.+)";
  static String exception_name_regex = "^.+Exception";
  
  public static boolean matches_stacktrace(String raw_stack_trace_content)
  {
    boolean matched = false;
    try
    {
      Pattern pattern = Pattern.compile(stacktrace_regex);
      Matcher matcher = pattern.matcher(raw_stack_trace_content);
      if (matcher.find()) {
        matched = true;
      }
    }
    catch (Exception localException)
    {
      matched = false;
    }
    return matched;
  }
  
  public static boolean matches_exception_name(String token)
  {
    boolean matched = false;
    try
    {
      Pattern pattern = Pattern.compile(exception_name_regex);
      Matcher matcher = pattern.matcher(token);
      if (matcher.find()) {
        matched = true;
      }
    }
    catch (Exception localException)
    {
      matched = false;
    }
    return matched;
  }
}
