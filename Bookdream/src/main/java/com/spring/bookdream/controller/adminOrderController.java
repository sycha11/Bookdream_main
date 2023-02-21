package com.spring.bookdream.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.bookdream.service.DeliveryService;
import com.spring.bookdream.service.OrderService;
import com.spring.bookdream.service.PayService;
import com.spring.bookdream.service.PurchaseService;
import com.spring.bookdream.vo.DeliveryVO;
import com.spring.bookdream.vo.OrderVO;
import com.spring.bookdream.vo.PageVO;
import com.spring.bookdream.vo.PayVO;
import com.spring.bookdream.vo.SearchCriteria;



@Controller
@RequestMapping("/admin")
public class adminOrderController {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private PayService payService;
	
	@Autowired
	private PurchaseService purchaseService;	

	@Autowired
	private DeliveryService deliveryService;
	
	// 주문현황 페이지 이동
	@RequestMapping(value="/order")
	public String orderDshbr() {

		return "admin/order_dshbr";
		
		}	
	
	// 주문관리 페이지 이동
	@RequestMapping(value="/orderMngmn")
	public String orderMngmn() {

		return "admin/order_mngmn";
		
		}		
	
	/*********************************************************/
	/********************  주문현황 페이지시작  *******************/	
	/*********************************************************/
	
	@RequestMapping(value="/orderStatusCount")
	@ResponseBody
	public List<Map<String, Object>> orderStatusCount(OrderVO order, DeliveryVO delivery) {

		// 배송중 -> 배송완료 갱신
		deliveryService.cmpltDelivery(delivery);		
		
		order.setAdmin("admin");	
		List<Map<String, Object>> cnt = orderService.orderStatusCount(order);
		return cnt;
		
		}	
	
	// 주간 주문 카운트
	@RequestMapping(value="/wekOrder")
	@ResponseBody
	public List<Map<String, Object>> orderWekDateCount(OrderVO order) {
		
		List<Map<String, Object>> dt = orderService.orderDateCount(order);
		List<Map<String, Object>> cdt = orderService.orderCancelDateCount(order);
		
		List<Map<String, Object>> combinedList = new ArrayList<>();
		
		// 두 리스트 중 작은 크기만큼 순회하면서 같은 인덱스에 있는 Map을 합칩니다.
		int minSize = Math.min(dt.size(), cdt.size());
		for (int i = 0; i < minSize; i++) {
		    Map<String, Object> map1 = dt.get(i);
		    Map<String, Object> map2 = cdt.get(i);

		    // 두 Map을 합쳐서 새로운 Map을 만듭니다.
		    Map<String, Object> combinedMap = new HashMap<>(map1);
		    combinedMap.putAll(map2);

		    // 합쳐진 Map을 결과 리스트에 추가합니다.
		    combinedList.add(combinedMap);
		}
		
		return combinedList;
		
		}
	
	// 월간 주문 카운트
	@RequestMapping(value="/mlyOrder")
	@ResponseBody
	public List<Map<String, Object>> orderMlyDateCount(OrderVO order) {
		
		List<Map<String, Object>> dt = orderService.orderMlyDateCount(order);
		List<Map<String, Object>> cdt = orderService.orderMlyCancelDateCount(order);

		List<Map<String, Object>> combinedList = new ArrayList<>();
		
		// 두 리스트 중 작은 크기만큼 순회하면서 같은 인덱스에 있는 Map을 합칩니다.
		int minSize = Math.min(dt.size(), cdt.size());
		for (int i = 0; i < minSize; i++) {
		    Map<String, Object> map1 = dt.get(i);
		    Map<String, Object> map2 = cdt.get(i);

		    // 두 Map을 합쳐서 새로운 Map을 만듭니다.
		    Map<String, Object> combinedMap = new HashMap<>(map1);
		    combinedMap.putAll(map2);

		    // 합쳐진 Map을 결과 리스트에 추가합니다.
		    combinedList.add(combinedMap);
		}
		
		return combinedList;
		
		}

	// 주간 결제 금액
	@RequestMapping(value="/wekPay")
	@ResponseBody
	public List<Map<String, Object>> payWekTotalPrice(PayVO pay) {
		
		List<Map<String, Object>> tp = payService.payWekTotalPrice(pay);

		return tp;
		
		}
	
	// 월간 결제 금액
	@RequestMapping(value="/MlyPay")
	@ResponseBody
	public List<Map<String, Object>> payMlyTotalPrice(PayVO pay) {
		
		List<Map<String, Object>> tp = payService.payMlyTotalPrice(pay);

		return tp;
		
	}	
	
	// 총 결제 금액
	@RequestMapping(value="/totalPayment")
	@ResponseBody
	public Map<String, Object> totalPaymentAmount(PayVO pay) {
		
		Map<String, Object> tpa = payService.totalPaymentAmount(pay);

		return tpa;
		
	}	
	
	// 구매도서수 비율
	@RequestMapping(value="/purchaseBookRatio")
	@ResponseBody
	public Map<String, Object> purchaseBookRatio() {
		
		Map<String, Object> data = purchaseService.purchaseBookRatio();

		return data;
		
	}				

	// 결제수단 비율
	@RequestMapping(value="/payMethodRate")
	@ResponseBody
	public Map<String, Object> payMethodRate() {
		
		Map<String, Object> data = payService.payMethodRate();

		return data;
		
	}	
	
	
	/*********************************************************/
	/********************  주문관리 페이지시작  *******************/	
	/*********************************************************/

	// 취소/반품 승인
	@RequestMapping(value="/orderAprvl")
	@ResponseBody
	public void orderAprvl(@RequestBody OrderVO order) {
		
		System.out.println("---> 결제취소/반품 승인 <---");
		orderService.orderAprvl(order);
		
		System.out.println("---> 사용,적립 포인트 반환 <---");
		orderService.updateUserPoint(order);
		    
		System.out.println("---> 도서 재고 반환 <---");
		orderService.updateBookStock(order);			
		
	}
	
	// 주문 총 관리 페이지
	@RequestMapping(value="/mngmn")
	@ResponseBody
	public OrderVO orderMngmn(@RequestParam("pageNum")  int pageNum, 
							  @RequestParam("srchCrtr") String srchCrtr, 
							  @RequestParam("srchKey")  String srchKey, 
							  @RequestParam("order_status") int orderStatus,
							  OrderVO order, SearchCriteria cri,
							  DeliveryVO delivery) {
		
		// 배송중 -> 배송완료 갱신
		deliveryService.cmpltDelivery(delivery);
		
		order.setSrchCrtr(srchCrtr);
		order.setSrchKey(srchKey);
		order.setPageNum(pageNum);
		order.setOrder_status(orderStatus);
		
		cri.setPageNum(pageNum);		
		
		// 한 페이지의 표시 개수
		order.setAmount(5);
		cri.setAmount(5);	
		
		int cnt = orderService.orderMngmnCount(order);	
	
		List<Map<String, Object>> list = orderService.orderMngmn(order);
		
		PageVO pageMaker = new PageVO(cri, cnt);
		
		OrderVO result = new OrderVO();
		result.setPage(pageMaker);
		result.setList(list);	
				
		return result;			
		
	}
	
	// 주문 총 관리 페이지 : 상세조회
	@RequestMapping(value="/mngmnDtls")
	@ResponseBody
	public List<Map<String, Object>> orderMngmn(@RequestParam("order_no") int order_no, OrderVO order) {
		
		order.setOrder_no(order_no);
		
		List<Map<String, Object>> list = orderService.orderMngmnDtls(order);
		
		return list;			
		
	}	
	
	// 송장번호 및 택배사 등록후 등록버튼 클릭
	@RequestMapping(value="/updateDelivery")
	@ResponseBody
	public void insertDelivery(@RequestBody DeliveryVO delivery) {
					
		// 송장번호 및 택배사 등록
		// 결제완료 -> 배송중으로 변경
		deliveryService.updateDelivery(delivery);
		
	}	
}
