package com.sharetreats;

import com.sharetreats.department.*;
import com.sharetreats.exception.CustomRuntimeException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DepartmentMain {

    private static final String MESSAGE =
            "\n===================================================================================================\n" +
                    "회사 조직 인원수 조회 서비스를 시작합니다. \n" +
                    "부서를 등록하고, 부서 간의 관계를 설정한 뒤 조직의 인원 수를 확인할 수 있습니다. \n" +
                    "\n" +
                    "1. 명령어 입력 매뉴얼 \n" +
                    "\n" +
                    "- 신규 부서 입력: [부서이름], [인원수]\n " +
                    "e.g. BACKEND, 10 \n" +
                    "부서이름은 대문자 알파벳만 가능합니다. 이미 부서 이름이 존재하거나, 인원수가 제대로 입력되지 않은 경우 예외가 발생합니다. \n" +
                    "\n" +
                    "- 부서 관계 설정: [상위부서]>[하위부서]\n" +
                    "e.g. DEV>BACKEND 또는 *>DEV \n" +
                    "*는 최상위 부서를 의미합니다. 최상위 부서가 설정되어 있지 않더라도, 부서 간의 상하관계는 설정 가능합니다. \n" +
                    "최상위 부서가 이미 설정되어 있는 부서는 최상위부서로 만들 수 없습니다. \n" +
                    "\n" +
                    "- 부서 조회: [부서이름]\n" +
                    "e.g. DEV\n" +
                    "부서 이름을 조회할 수 있습니다. 이 때 조회한 부서와 그 부서를 포함하고 있는 최상위 부서, 해당 부서의 모든 인원수가 함께 출력됩니다. \n" +
                    "최상위 부서가 없는 경우라면 해당 부서의 상위 부서 중 가장 높은 부서가 출력되고, 해당 부서의 모든 인원수가 함께 출력됩니다. \n" +
                    "\n" +
                    "예를 들어 IT, 20 / DEV, 0 / BACKEND, 10 와 같이 세 부서를 입력하고 \n" +
                    "*>IT / IT>DEV / DEV>BACKEND 와 같이 관계를 설정했을 때, *>IT>DEV>BACKEND 형태의 조직도가 설정됩니다. \n" +
                    "위 조직도에 속해 있는 특정 부서를 조회했을 때 출력은 모두 같으며, 다음과 같습니다. \n" +
                    "현재부서: [ 조회부서 ], 최상위부서: [ IT ], 총 인원: [ 40 ] \n" +
                    "\n" +
                    "만약 최상위 부서가 입력되지 않고 부서의 관계가 형성되는 경우, 해당 부서 중 가장 높은 부서와 부서 내의 총 인원수가 출력됩니다. \n" +
                    "예를 들어 A, 10 / B, 10 / C, 10 입력 후 최상위 부서 없이 A>B>C 로 관계를 설정하는 경우, A,B,C 어떤 부서를 조회하더라도, 상위부서는 A, 인원 수는 30명으로 조회가 됩니다. \n" +
                    "\n" +
                    "2. 부서 이동 설정\n" +
                    "\n" +
                    "최상위 부서가 아닌 부서는 다른 부서의 하위 부서로 재설정될 수 있습니다. 이 때 부서 인원도 자동으로 재설정됩니다. \n" +
                    "예를 들어 위의 예시에서 D, 20가 추가되고 *>D를 한 뒤 D>A를 하면, *>D>A>B>C의 조직도가 형성되고, 부서의 총 인원수도 50으로 조정됩니다. \n" +
                    "만약 A가 아닌 D>B를 하게 되면, A는 조직에서 제외되어 *>D>B>C의 조직이 형성되고, 인원수는 40이 됩니다. A 조직은 사라지지는 않습니다. \n" +
                    "\n" +
                    "*>DEV(10), DEV>FRONTEND(20), DEV>BACKEND(30), DEV>DEVOPS(20) 총 80명이 포함된 조직이 기본으로 저장되어 있습니다.";;

    public static void main(String[] args) throws IOException {

        DepartmentRepository departmentRepository = new DepartmentRepositoryImpl();
        DepartmentService departmentService = new DepartmentServiceImpl(departmentRepository);
        DepartmentController departmentController = new DepartmentController(departmentService);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(MESSAGE);

        while (true) {

            String input = br.readLine();
            try {
                String result = departmentController.parseCommand(input);
                System.out.println(result);
            } catch (CustomRuntimeException e) {
                if(e.getCause() != null) System.out.println(e.getCause().getMessage());
                System.out.println(e.getMessage());
            }

        }

    }

}
