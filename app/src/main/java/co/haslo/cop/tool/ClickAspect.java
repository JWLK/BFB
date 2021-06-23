package co.haslo.cop.tool;

import android.view.View;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class ClickAspect {

    @Pointcut("execution(* android.view.View.OnClickListener.*(..))")
    public void onClickPoincut(){}

    @Before("onClickPoincut()")
    public void beforeOnClick(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        if(args.length > 0){
            View view = (View) args[0];
            view.setEnabled(false);
        }
    }

    @After("onClickPoincut()")
    public void atferOnClick(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        if(args.length > 0){
            final View view = (View) args[0];
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.setEnabled(true);
                }
            }, 500);
        }
    }
}