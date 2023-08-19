export type errorType = {
    code: number;
    message: string;
    exceptionType: string;
};

export type WinNumber = {
    time: string;
    date: string;
    prize: number;
    winner: number;
    topNumberList: number[];
    bonus: number;
};

export type Res = {
    code: number;
    message: string;
    data: any;
};

export type UserIfState = {
    email: string;
    nickname: string;
    cash: string;
    role: string;
    status: string;
    statement: Record<string, any>;
};

export type UserAllIf = {
    id: number;
    email: string;
    password: string;
    nickname: string;
    cash: string;
    role: string;
    status: string;
    statement: Record<string, any>;
};

export type AdminGetCharges = {
    userId: Number,
    msg: string,
    value: number,
};