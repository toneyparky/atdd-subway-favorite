package wooteco.subway.web.member.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class MemberValidateAspect {

	@Pointcut("execution(* wooteco.subway.web.member.MemberController.*(..)) " +
			"&& !@annotation(wooteco.subway.web.member.aspect.NoValidate)")
	// TODO: 2020/05/21 @target이 아니라 @annotation이다.
	// TODO: 2020/05/21 그냥 target은 예약어라 아래 @Around에서 안먹힌다.
	public void valideTarget() {
	}

	@Around("valideTarget()")
	public Object validate(ProceedingJoinPoint joinPoint) throws Throwable {
		System.out.println("유안 멋져");
		Object[] args = joinPoint.getArgs();

		for (Object arg : args) {
			System.out.println(arg.toString());
		}

		return joinPoint.proceed();
	}

}
