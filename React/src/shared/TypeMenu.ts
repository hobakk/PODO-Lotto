export type UnifiedResponse<T> = {
    code: number,
    msg: string,
    data?: T,
};

export type Err = {
    code: number;
    msg: string;
    exceptionType: string;
};

export type Res = {
    code: number;
    msg: string;
    data: any;
};

export type WinNumber = {
    time: string;
    date: string;
    prize: number;
    winner: number;
    topNumberList: number[];
    bonus: number;
};

export type SixNumber = {
    id: number;
    userId: number;
    buyDate: Date;
    numberList?: string[];
};

export type UserIfState = {
    email: string;
    nickname: string;
    cash: number;
    role: string;
    status: string;
    statement: Record<string, any>;
};

export type UserAllIf = {
    id: number;
    email: string;
    password: string;
    nickname: string;
    cash: number;
    role: string;
    status: string;
    statement: Record<string, any>;
};

export type AdminGetCharges = {
    userId: number,
    msg: string,
    value: number,
};

export type upDownCashRequest = {
    userId: number,
    msg: string,
    cash: number,
}