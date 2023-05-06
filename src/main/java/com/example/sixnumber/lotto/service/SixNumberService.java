package com.example.sixnumber.lotto.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.dto.ListApiResponse;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.lotto.dto.BuyNumberRequest;
import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.entity.SixNumber;
import com.example.sixnumber.lotto.repository.LottoRepository;
import com.example.sixnumber.lotto.repository.SixNumberRepository;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.UserRepository;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class SixNumberService {

	private final SixNumberRepository sixNumberRepository;
	private final LottoRepository lottoRepository;
	private final UserRepository userRepository;
	private final Random rd = new Random();

	public ListApiResponse<Integer> buyNumber(BuyNumberRequest buyNumberRequest, HttpServletRequest httpServletRequest) {
		User user = findByTokenByUser(httpServletRequest);
		String now = toDay();

		List<Integer> numberList = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			numberList.add(rd.nextInt(45) + 1);
			if (numberList.size() == 6) {
				break;
			}
		}
		SixNumber sixNumber = new SixNumber(user.getId(), now, numberList);
		sixNumberRepository.save(sixNumber);

		// 스캐줄러로 뺄지 말지 고민중 이유 : 한개의 서비스 로직에서 너무 많은 저장이 이루어짐. 영속성 컨텍스트 각이 나오는지도 보고있음
		// 방법 : 저장된 sixNumber 를 시간단위로 모아 한번에 스케줄러로 처리 병렬을 지원하는 java.util.concurrent.ScheduledExecutorService 사용
		Lotto lotto = lottoRepository.findById(0L)
			.orElseThrow(()-> new IllegalArgumentException("존재하지 않는 정보"));
		List<Integer> countList = lotto.getCountList();
		// for (int num : numberList) {
		// 	countList.get(num -1) ++;
		// }
		lottoRepository.save(lotto);

		// 저장할 필요가 있는지 점검할 필요가 있음
		user.setCash("-", buyNumberRequest.getValue() * 200);
		userRepository.save(user);
		return ListApiResponse.ok("요청 성공", numberList);
	}

	private User findByTokenByUser(HttpServletRequest request) {
		Claims claims;
		String token = JwtProvider.resolveToken(request);
		if (token != null) {
			JwtProvider.validateToken(token);
			claims = JwtProvider.getClaims(token);
			String email = claims.getSubject();
			return findByUser(email);
		} else { throw  new IllegalArgumentException("유효하지 않은 토큰"); }
	}

	private User findByUser(String email) {
		return userRepository.findUserByEmail(email)
			.orElseThrow(()-> new IllegalArgumentException("아이디 또는 비밀번호를 잘못 입력하셨습니다"));
	}

	private String toDay() {
		Date today = new Date();
		SimpleDateFormat yd = new SimpleDateFormat("yyyy-MM");
		return yd.format(today);
	}
}
